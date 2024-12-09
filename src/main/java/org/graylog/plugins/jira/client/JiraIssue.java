package org.graylog.plugins.jira.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JiraIssue {

    private final String projectKey;
    private final String summary;
    private final String description;
    private final String issueType;
    private final String assigneeName;
    private final String priority;
    private final Set<String> labels;
    private final Set<String> components;
    private final String environment;
    private final String graylogHashCustomField;
    private final String graylogHashRegex;
    private final Map<String, String> customFields;
    private String graylogHash;

    public JiraIssue(String projectKey, String summary, String description, String issueType, String assigneeName,
            String priority, Set<String> labels, Set<String> components, String environment,
            String graylogHashCustomField, String graylogHashRegex, Map<String, String> customFields) {
        this.projectKey = projectKey;
        this.summary = summary;
        this.description = description;
        this.issueType = issueType;
        this.assigneeName = assigneeName;
        this.priority = priority;
        this.labels = labels;
        this.components = components;
        this.environment = environment;
        this.graylogHashCustomField = graylogHashCustomField;
        this.graylogHashRegex = graylogHashRegex;
        this.customFields = customFields;
    }

    public String toJsonString() {
        final Map<String, Object> params = new HashMap<>();
        params.put("project", ImmutableMap.of("key", projectKey.trim()));
        params.put("summary", summary);
        params.put("description", description);
        params.put("issuetype", ImmutableMap.of("name", issueType.trim()));
        if (!Strings.isNullOrEmpty(assigneeName)) {
            params.put("assignee", ImmutableMap.of("name", assigneeName.trim()));
        }
        if (!Strings.isNullOrEmpty(priority)) {
            params.put("priority", ImmutableMap.of("name", priority.trim()));
        }
        if (labels != null && !labels.isEmpty()) {
            params.put("labels", labels);
        }
        if (components != null && !components.isEmpty()) {
            params.put("components", components.stream()
                    .map(c -> ImmutableMap.of("name", c.trim()))
                    .collect(Collectors.toList()));
        }
        if (!Strings.isNullOrEmpty(environment)) {
            params.put("environment", environment.trim());
        }

        // Custom fields
        if (!Strings.isNullOrEmpty(graylogHashCustomField)) {
            params.put(graylogHashCustomField, createGraylogHash());
        }
        customFields.forEach(params::putIfAbsent);

        try {
            return new ObjectMapper().writeValueAsString(ImmutableMap.of("fields", params));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to build Jira issue payload as JSON format.", e);
        }
    }

    public String createGraylogHash() {
        if (graylogHash != null) {
            return graylogHash;
        }

        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        final String value;
        if (graylogHashRegex == null) {
            value = description;
        } else {
            final Pattern pattern = Pattern.compile(graylogHashRegex);
            final Matcher matcher = pattern.matcher(description);

            final StringBuilder sb = new StringBuilder();
            if (matcher.find()) {
                int i = 0;
                do {
                    sb.append(matcher.group(i++));
                } while (i < matcher.groupCount());
            }
            value = sb.length() > 0 ? sb.toString() : description;
        }

        md.update(value.getBytes(Charset.defaultCharset()));
        graylogHash = DatatypeConverter.printHexBinary(md.digest());
        return graylogHash;
    }
}
