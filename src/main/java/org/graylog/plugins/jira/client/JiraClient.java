package org.graylog.plugins.jira.client;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.floreysoft.jmte.Engine;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import org.graylog.plugins.jira.event.notifications.JiraEventNotificationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
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

    public void createIssue(final JiraEventNotificationConfig config, final Map<String, Object> model) {
        final OkHttpClient client;
        if (Strings.isNullOrEmpty(config.proxyURL())) {
            client = new OkHttpClient();
        } else {
            client = new OkHttpClient.Builder().proxy(buildProxy(config.proxyURL())).build();
        }

        final JiraIssue jiraIssue = createIssueCreationRequest(config, model);

        if (!Strings.isNullOrEmpty(config.searchGraylogHashField())) {
            final String duplicateIssueId = searchForDuplicateIssue(client, config, jiraIssue);
            if (duplicateIssueId != null) {
                LOG.debug("Duplicate JIRA issue detected with {} - issue will not be created", duplicateIssueId);
                if (!Strings.isNullOrEmpty(config.duplicateIssueComment())) {
                    addIssueComment(client, config, duplicateIssueId, model);
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
                + "AND \"" + parseGraylogHashField(config.searchGraylogHashField())[1]
                + "\" ~ \"" + jiraIssue.createGraylogHash() + "\"";

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
            final Map<String, Object> model) {
        model.put("graylog_url", config.graylogURL());
        return new JiraIssue(
                config.projectKey(),
                buildMessage(config.issueSummary(), model),
                buildMessage(config.issueDescription(), model),
                config.issueType(),
                config.issueAssigneeName(),
                config.issuePriority(),
                parseDelimitedValues(config.issueLabels()),
                parseDelimitedValues(config.issueComponents()),
                config.issueEnvironment(),
                parseGraylogHashField(config.searchGraylogHashField())[0],
                config.searchGraylogHashRegex(),
                parseMapValues(config.issueCustomFields())
        );
    }

    private Proxy buildProxy(final String proxyURL) {
        try {
            final URI uri = new URI(proxyURL);
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(uri.getHost(), uri.getPort()));
        } catch (final URISyntaxException e) {
            throw new JiraClientException("Proxy URL is invalid format. Proxy URL=" + proxyURL, e);
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
    private String[] parseGraylogHashField(final String graylogHashField) {
        if (Strings.isNullOrEmpty(graylogHashField)) {
            return new String[] {"", ""};
        }
        if (!graylogHashField.contains("=")) {
            throw new JiraClientException("Graylog hash field is incorrectly formed.");
        }
        return graylogHashField.split("=", 2);
    }

    private Map<String, String> parseMapValues(final String mapString) {
        if (Strings.isNullOrEmpty(mapString)) {
            return new HashMap<>();
        }
        return Splitter.on(',').withKeyValueSeparator('=').split(mapString);
    }
}
