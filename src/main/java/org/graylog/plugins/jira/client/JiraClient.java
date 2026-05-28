package org.graylog.plugins.jira.client;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.floreysoft.jmte.Engine;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import org.graylog.events.notifications.EventNotificationModelData;
import org.graylog.plugins.jira.event.notifications.JiraEventNotificationConfig;
import org.graylog2.jackson.TypeReferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.xml.bind.DatatypeConverter;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JiraClient {

    private static final Logger LOG = LoggerFactory.getLogger(JiraClient.class);
    private static final String HEADER_AUTH = "Authorization";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final Engine templateEngine;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    public JiraClient(final Engine engine) {
        this.templateEngine = engine;
    }

    public void createIssue(final JiraEventNotificationConfig config, final EventNotificationModelData model) {
        final OkHttpClient client;
        if (Strings.isNullOrEmpty(config.proxyURL())) {
            client = new OkHttpClient();
        } else {
            client = new OkHttpClient.Builder().proxy(buildProxy(config.proxyURL())).build();
        }


        final Map<String, Object> templateData = objectMapper.convertValue(model, TypeReferences.MAP_STRING_OBJECT);
        templateData.put("graylog_url", config.graylogURL());
        final JiraIssue jiraIssue = createIssueCreationRequest(config, model, templateData);

        if (!Strings.isNullOrEmpty(config.searchGraylogHashField())) {
            final String duplicateIssueId = searchForDuplicateIssue(client, config, jiraIssue);
            if (duplicateIssueId != null) {
                LOG.debug("Duplicate JIRA issue detected with {} - issue will not be created", duplicateIssueId);
                if (!Strings.isNullOrEmpty(config.duplicateIssueComment())) {
                    addIssueComment(client, config, duplicateIssueId, templateData);
                }
                return;
            }
        }

        // Create issue
        final HttpUrl url = constructURL(config.jiraURL(), "rest/api/2/issue");
        final RequestBody reqBody = RequestBody.create(jiraIssue.toJsonString(), JSON);
        final Request req = new Request.Builder()
                .url(url)
                .addHeader(HEADER_AUTH, basicAuthHeaderValue(config))
                .post(reqBody)
                .build();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Request: {}", req);
        }

        // Response
        try (final Response res = client.newCall(req).execute()) {
            if (res.body() == null) {
                throw new JiraClientException("Jira (issue create) returned null body");
            }
            if (!res.isSuccessful()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(res.toString());
                }
                throw new JiraClientException("Jira (issue create) returned client error. HTTP Status=" + res.code()
                        + ", response=" + res.body().string());
            }
        } catch (final IOException ex) {
            throw new JiraClientException("Failed to send POST request to Jira (issue create).", ex);
        }
    }

    private HttpUrl constructURL(final String jiraURL, final String apiPart) {
        String baseUrl = jiraURL;
        if (!jiraURL.endsWith("/")) {
            baseUrl += "/";
        }
        final HttpUrl url = HttpUrl.parse(baseUrl + apiPart);
        if (url == null) {
            throw new JiraClientException("Jira URL is in invalid format. URL=" + jiraURL);
        }
        return url;
    }

    /**
     * @return ID of first duplicate issue
     */
    private String searchForDuplicateIssue(final OkHttpClient client, final JiraEventNotificationConfig config,
                                           final JiraIssue jiraIssue) {
        final String jql = "project = " + config.projectKey()
                           + (Strings.isNullOrEmpty(config.searchFilterJQL()) ? " " : " " + config.searchFilterJQL() + " ")
                           + "AND \"" + parseJiraField(config.searchGraylogHashField())[1]
                           + "\" ~ \"" + jiraIssue.getMessageHash() + "\"";

        final HttpUrl url = constructURL(config.jiraURL(), "rest/api/2/search").newBuilder()
                .addQueryParameter("jql", jql)
                .addQueryParameter("startAt", "0")
                .addQueryParameter("maxResults", "1")
                .addQueryParameter("fields", "id,key")
                .build();
        final Request req = new Request.Builder()
                .url(url)
                .addHeader(HEADER_AUTH, basicAuthHeaderValue(config))
                .get()
                .build();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Request: {}", req);
        }

        try (final Response res = client.newCall(req).execute()) {
            if (res.body() == null) {
                throw new JiraClientException("Jira (issue search) returned null body");
            }
            if (!res.isSuccessful()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(res.toString());
                }
                throw new JiraClientException("Jira (issue search) returned client error. HTTP Status=" + res.code()
                        + ", response=" + res.body().string());
            }
            final String jsonData = res.body().string();
            final JsonNode issues = objectMapper.readValue(jsonData, JsonNode.class).get("issues");
            if (issues != null && issues.isArray() && !issues.isEmpty()) {
                return issues.get(0).get("id").textValue();
            }
            return null; // no duplicates found
        } catch (final JacksonException ex) {
            throw new IllegalStateException("Failed to read JIRA (issue search) response body.", ex);
        } catch (final IOException ex) {
            throw new JiraClientException("Failed to send GET request to JIRA (issue search).", ex);
        }
    }

    private void addIssueComment(final OkHttpClient client, final JiraEventNotificationConfig config,
            final String issueId, final Map<String, Object> model) {
        final HttpUrl url = constructURL(config.jiraURL(), "rest/api/2/issue/" + issueId + "/comment").newBuilder()
                .build();

        final RequestBody reqBody = RequestBody.create("{\"body\":\"" + buildMessage(config.duplicateIssueComment(), model) + "\"}", JSON);
        final Request req = new Request.Builder()
                .url(url)
                .addHeader(HEADER_AUTH, basicAuthHeaderValue(config))
                .post(reqBody)
                .build();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Request: {}", req);
        }

        try (final Response res = client.newCall(req).execute()) {
            if (res.body() == null) {
                throw new JiraClientException("Jira (issue comment) returned null body");
            }
            if (!res.isSuccessful()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(res.toString());
                }
                throw new JiraClientException("Jira (issue comment) returned client error. HTTP Status=" + res.code()
                        + ", response=" + res.body().string());
            }
        } catch (final IOException ex) {
            throw new JiraClientException("Failed to send POST request to JIRA (issue comment).", ex);
        }
    }

    private String basicAuthHeaderValue(final JiraEventNotificationConfig config) {
        final String auth = config.credUsername() + ":" + config.credPassword();
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }

    private JiraIssue createIssueCreationRequest(final JiraEventNotificationConfig config,
                                                 final EventNotificationModelData model,
                                                 final Map<String, Object> templateData) {
        final String issueDesc = buildMessage(config.issueDescription(), templateData);

        String messageHash = createMessageHash(config, model, issueDesc);

        return new JiraIssue(
            config.projectKey(),
            buildMessage(config.issueSummary(), templateData),
            issueDesc,
            config.issueType(),
            config.issueAssigneeName(),
            config.issuePriority(),
            parseDelimitedValues(config.issueLabels()),
            parseDelimitedValues(config.issueComponents()),
            config.issueEnvironment(),
            parseJiraField(config.searchGraylogHashJiraField())[0],
            messageHash,
            parseMapValues(config.issueCustomFields())
        );
    }

    public String createMessageHash(JiraEventNotificationConfig config, EventNotificationModelData model, String issueDesc) {
        if (Strings.isNullOrEmpty(config.searchGraylogHashJiraField())) {
            return null;
        }
        if (config.searchGraylogHashField() != null) {
            final String msgHash = model.backlog().stream()
                .findFirst()
                .map(ms -> ms.getField(config.searchGraylogHashField()))
                .map(Object::toString)
                .orElse(null);
            if (!Strings.isNullOrEmpty(msgHash)) {
                return msgHash;
            }
        }
        String valueForHash = null;
        if (config.searchGraylogHashRegex() != null) {
            valueForHash = extractValueForHash(config.searchGraylogHashRegex(), issueDesc);
        }

        if (Strings.isNullOrEmpty(valueForHash)) {
            valueForHash = issueDesc; // use whole description since hash is required
        }
        if (valueForHash.isBlank()) {
            return null;
        }

        return calculateHash(valueForHash);
    }

    public String extractValueForHash(String graylogHashRegex, String description) {
        final Pattern pattern = Pattern.compile(graylogHashRegex);
        final Matcher matcher = pattern.matcher(description);

        final StringBuilder sb = new StringBuilder();
        if (matcher.find()) {
            int i = 0;
            do {
                sb.append(matcher.group(i++));
            } while (i < matcher.groupCount());
        }
        return !sb.isEmpty() ? sb.toString() : null;
    }

    public static String calculateHash(String text) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printHexBinary(md.digest());
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to create message hash", e);
        }
    }

    private Proxy buildProxy(final String proxyURL) {
        try {
            final URI uri = new URI(proxyURL);
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(uri.getHost(), uri.getPort()));
        } catch (final URISyntaxException e) {
            throw new JiraClientException("Proxy URL is in invalid format. Proxy URL=" + proxyURL, e);
        }
    }

    private String buildMessage(final String msgTemplate, final Map<String, Object> model) {
        final String template;
        if (Strings.isNullOrEmpty(msgTemplate)) {
            template = JiraEventNotificationConfig.DEFAULT_ISSUE_DESCRIPTION;
        } else {
            template = msgTemplate;
        }
        return templateEngine.transform(template, model).replaceAll("\\r\\n|\\n|\\r", "\n");
    }

    private Set<String> parseDelimitedValues(final String delimitedString) {
        if (Strings.isNullOrEmpty(delimitedString)) {
            return new HashSet<>();
        }
        return Arrays.stream(delimitedString.split(";")).collect(Collectors.toSet());
    }

    /**
     * @return array of custom field id and it's name
     */
    private String[] parseJiraField(final String jiraField) {
        if (Strings.isNullOrEmpty(jiraField)) {
            return new String[] {"", ""};
        }
        if (!jiraField.contains("=")) {
            throw new JiraClientException("Jira field is incorrectly formed. Expected '{id}={name}'");
        }
        return jiraField.split("=", 2);
    }

    private Map<String, String> parseMapValues(final String mapString) {
        if (Strings.isNullOrEmpty(mapString)) {
            return new HashMap<>();
        }
        return Splitter.on(',').withKeyValueSeparator('=').split(mapString);
    }
}
