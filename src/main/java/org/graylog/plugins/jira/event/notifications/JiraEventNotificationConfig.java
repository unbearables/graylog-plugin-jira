package org.graylog.plugins.jira.event.notifications;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import org.apache.commons.lang3.StringUtils;
import org.graylog.events.contentpack.entities.EventNotificationConfigEntity;
import org.graylog.events.event.EventDto;
import org.graylog.events.notifications.EventNotificationConfig;
import org.graylog.events.notifications.EventNotificationExecutionJob;
import org.graylog.scheduler.JobTriggerData;
import org.graylog2.contentpacks.EntityDescriptorIds;
import org.graylog2.contentpacks.model.entities.references.ValueReference;
import org.graylog2.plugin.rest.ValidationResult;

import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@JsonIgnoreProperties(ignoreUnknown = true)
@AutoValue
@JsonTypeName(JiraEventNotificationConfig.TYPE_NAME)
@JsonDeserialize(builder = JiraEventNotificationConfig.Builder.class)
public abstract class JiraEventNotificationConfig implements EventNotificationConfig {

    public static final String TYPE_NAME = "jira-notification";

    // Plugin input fields
    public static final String FIELD_JIRA_URL = "jira_url";
    public static final String FIELD_PROXY_URL = "proxy_url";
    public static final String FIELD_GRAYLOG_URL = "graylog_url";
    public static final String FIELD_CRED_USERNAME = "cred_username";
    public static final String FIELD_CRED_PWD = "cred_password";
    public static final String FIELD_PROJECT_KEY = "project_key";
    public static final String FIELD_ISSUE_TYPE = "issue_type";
    public static final String FIELD_ISSUE_ASSIGNEE_NAME = "issue_assignee_name";
    public static final String FIELD_ISSUE_PRIORITY = "issue_priority";
    public static final String FIELD_ISSUE_LABELS = "issue_labels";
    public static final String FIELD_ISSUE_COMPONENTS = "issue_components";
    public static final String FIELD_ISSUE_ENVIRONMENT = "issue_environment";
    public static final String FIELD_ISSUE_CUSTOM_FIELDS = "issue_custom_fields";
    public static final String FIELD_ISSUE_SUMMARY = "issue_summary";
    public static final String FIELD_ISSUE_DESCRIPTION = "issue_description";
    public static final String FIELD_SEARCH_GRAYLOG_HASH_FIELD = "search_graylog_hash_field";
    public static final String FIELD_SEARCH_GRAYLOG_HASH_REGEX = "search_graylog_hash_regex";
    public static final String FIELD_SEARCH_FILTER_JQL = "search_filter_jql";
    public static final String FIELD_DUPLICATE_ISSUE_COMMENT = "duplicate_issue_comment";

    // Default values
    public static final String DEFAULT_ISSUE_SUMMARY = "Graylog log error - ${event.id}";
    public static final String DEFAULT_ISSUE_DESCRIPTION =
            "--- [Event Definition] ---\n" +
            "*ID:* ${event_definition_id}\n" +
            "*Type:* ${event_definition_type}\n" +
            "*Title:* ${event_definition_title}\n" +
            "*Description:* ${event_definition_description}\n" +
            "--- [Event] ---\n" +
            "*Event:* ${event}\n" +
            "--- [Event Detail] ---\n" +
            "*Timestamp:* ${event.timestamp}\n" +
            "*Message:* ${event.message}\n" +
            "*Source:* ${event.source}\n" +
            "*Key:* ${event.key}\n" +
            "*Priority:* ${event.priority}\n" +
            "*Alert:* ${event.alert}\n" +
            "*Timestamp Processing:* ${event.timestamp}\n" +
            "*TimeRange Start:* ${event.timerange_start}\n" +
            "*TimeRange End:* ${event.timerange_end}\n" +
            "${if event.fields}\n" +
            "*Fields:*\n" +
            "${foreach event.fields field} ${field.key}: ${field.value}\n" +
            "${end}\n" +
            "${if backlog}\n" +
            "--- [Backlog] ---\n" +
            "*Messages:*\n" +
            "${foreach backlog message}\n" +
            "Graylog link: ${graylog_url}/messages/${message.index}/${message.id}\n" +
            "```\n" +
            "${message}\n" +
            "```\n" +
            "${end}\n" +
            "${end}";

    @JsonProperty(FIELD_JIRA_URL)
    @NotBlank
    public abstract String jiraURL();

    @JsonProperty(FIELD_PROXY_URL)
    public abstract String proxyURL();

    @JsonProperty(FIELD_GRAYLOG_URL)
    public abstract String graylogURL();

    @JsonProperty(FIELD_CRED_USERNAME)
    @NotBlank
    public abstract String credUsername();

    @JsonProperty(FIELD_CRED_PWD)
    @NotBlank
    public abstract String credPassword();

    @JsonProperty(FIELD_PROJECT_KEY)
    @NotBlank
    public abstract String projectKey();

    @JsonProperty(FIELD_ISSUE_TYPE)
    @NotBlank
    public abstract String issueType();

    @JsonProperty(FIELD_ISSUE_ASSIGNEE_NAME)
    public abstract String issueAssigneeName();

    @JsonProperty(FIELD_ISSUE_PRIORITY)
    public abstract String issuePriority();

    @JsonProperty(FIELD_ISSUE_LABELS)
    public abstract String issueLabels();

    @JsonProperty(FIELD_ISSUE_COMPONENTS)
    public abstract String issueComponents();

    @JsonProperty(FIELD_ISSUE_ENVIRONMENT)
    public abstract String issueEnvironment();

    @JsonProperty(FIELD_ISSUE_CUSTOM_FIELDS)
    public abstract String issueCustomFields();

    @JsonProperty(FIELD_ISSUE_SUMMARY)
    @NotBlank
    public abstract String issueSummary();

    @JsonProperty(FIELD_ISSUE_DESCRIPTION)
    @NotBlank
    public abstract String issueDescription();

    @JsonProperty(FIELD_SEARCH_GRAYLOG_HASH_FIELD)
    public abstract String searchGraylogHashField();

    @JsonProperty(FIELD_SEARCH_GRAYLOG_HASH_REGEX)
    public abstract String searchGraylogHashRegex();

    @JsonProperty(FIELD_SEARCH_FILTER_JQL)
    public abstract String searchFilterJQL();

    @JsonProperty(FIELD_DUPLICATE_ISSUE_COMMENT)
    public abstract String duplicateIssueComment();

    public static Builder builder() {
        return Builder.create();
    }

    @JsonIgnore
    @Override
    public JobTriggerData toJobTriggerData(final EventDto dto) {
        return EventNotificationExecutionJob.Data.builder().eventDto(dto).build();
    }

    @JsonIgnore
    @Override
    public ValidationResult validate() {
        final ValidationResult validation = new ValidationResult();

        if (jiraURL().isEmpty()) {
            validation.addError(FIELD_JIRA_URL, FIELD_JIRA_URL + " cannot be empty.");
        }
        if (!validURL(jiraURL())) {
            validation.addError(FIELD_JIRA_URL, FIELD_JIRA_URL + " is invalid format.");
        }
        if (!validURL(graylogURL())) {
            validation.addError(FIELD_GRAYLOG_URL, FIELD_GRAYLOG_URL + " is invalid format.");
        }
        if (!validURL(proxyURL())) {
            validation.addError(FIELD_PROXY_URL, FIELD_PROXY_URL + " is invalid format.");
        }
        if (credUsername().isEmpty()) {
            validation.addError(FIELD_CRED_USERNAME, FIELD_CRED_USERNAME + " cannot be empty.");
        }
        if (credPassword().isEmpty()) {
            validation.addError(FIELD_CRED_PWD, FIELD_CRED_PWD + " cannot be empty.");
        }
        if (projectKey().isEmpty()) {
            validation.addError(FIELD_PROJECT_KEY, FIELD_PROJECT_KEY + " cannot be empty.");
        }
        if (issueType().isEmpty()) {
            validation.addError(FIELD_ISSUE_TYPE, FIELD_ISSUE_TYPE + " cannot be empty.");
        }
        if (issueSummary().isEmpty()) {
            validation.addError(FIELD_ISSUE_SUMMARY, FIELD_ISSUE_SUMMARY + " cannot be empty.");
        }
        if (issueDescription().isEmpty()) {
            validation.addError(FIELD_ISSUE_DESCRIPTION, FIELD_ISSUE_DESCRIPTION + " cannot be empty.");
        }
        if (!searchGraylogHashField().isEmpty() && !searchGraylogHashField().contains("=")) {
            validation.addError(FIELD_SEARCH_GRAYLOG_HASH_FIELD, FIELD_SEARCH_GRAYLOG_HASH_FIELD + " is incorrectly filled.");
        }
        if (!searchGraylogHashRegex().isEmpty()) {
            try {
                Pattern.compile(searchGraylogHashRegex());
            } catch (final PatternSyntaxException e) {
                validation.addError(FIELD_SEARCH_GRAYLOG_HASH_REGEX, FIELD_SEARCH_GRAYLOG_HASH_REGEX + " is not a valid regex.");
            }
        }
        return validation;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @AutoValue.Builder
    public static abstract class Builder implements EventNotificationConfig.Builder<Builder> {

        @JsonCreator
        public static Builder create() {
            return new AutoValue_JiraEventNotificationConfig.Builder()
                    .type(TYPE_NAME)
                    .proxyURL("")
                    .graylogURL("")
                    .issuePriority("")
                    .issueLabels("")
                    .issueComponents("")
                    .issueEnvironment("")
                    .issueCustomFields("")
                    .issueSummary(DEFAULT_ISSUE_SUMMARY)
                    .issueDescription(DEFAULT_ISSUE_DESCRIPTION)
                    .searchGraylogHashField("")
                    .searchGraylogHashRegex("")
                    .searchFilterJQL("")
                    .duplicateIssueComment("");
        }

        @JsonProperty(FIELD_JIRA_URL)
        public abstract Builder jiraURL(String jiraURL);

        @JsonProperty(FIELD_PROXY_URL)
        public abstract Builder proxyURL(String proxyURL);

        @JsonProperty(FIELD_GRAYLOG_URL)
        public abstract Builder graylogURL(String graylogURL);

        @JsonProperty(FIELD_CRED_USERNAME)
        public abstract Builder credUsername(String credUsername);

        @JsonProperty(FIELD_CRED_PWD)
        public abstract Builder credPassword(String credPassword);

        @JsonProperty(FIELD_PROJECT_KEY)
        public abstract Builder projectKey(String projectKey);

        @JsonProperty(FIELD_ISSUE_TYPE)
        public abstract Builder issueType(String issueType);

        @JsonProperty(FIELD_ISSUE_ASSIGNEE_NAME)
        public abstract Builder issueAssigneeName(String issueAssigneeName);

        @JsonProperty(FIELD_ISSUE_PRIORITY)
        public abstract Builder issuePriority(String issuePriority);

        @JsonProperty(FIELD_ISSUE_LABELS)
        public abstract Builder issueLabels(String issueLabels);

        @JsonProperty(FIELD_ISSUE_COMPONENTS)
        public abstract Builder issueComponents(String issueComponents);

        @JsonProperty(FIELD_ISSUE_ENVIRONMENT)
        public abstract Builder issueEnvironment(String issueEnvironment);

        @JsonProperty(FIELD_ISSUE_CUSTOM_FIELDS)
        public abstract Builder issueCustomFields(String issueCustomFields);

        @JsonProperty(FIELD_ISSUE_SUMMARY)
        public abstract Builder issueSummary(String issueSummary);

        @JsonProperty(FIELD_ISSUE_DESCRIPTION)
        public abstract Builder issueDescription(String issueDescription);

        @JsonProperty(FIELD_SEARCH_GRAYLOG_HASH_FIELD)
        public abstract Builder searchGraylogHashField(String searchGraylogHashField);

        @JsonProperty(FIELD_SEARCH_GRAYLOG_HASH_REGEX)
        public abstract Builder searchGraylogHashRegex(String searchGraylogHashRegex);

        @JsonProperty(FIELD_SEARCH_FILTER_JQL)
        public abstract Builder searchFilterJQL(String searchFilterJQL);

        @JsonProperty(FIELD_DUPLICATE_ISSUE_COMMENT)
        public abstract Builder duplicateIssueComment(String duplicateIssueComment);

        public abstract JiraEventNotificationConfig build();
    }

    @Override
    public EventNotificationConfigEntity toContentPackEntity(final EntityDescriptorIds entityDescriptorIds) {
        return JiraEventNotificationConfigEntity.builder()
                .jiraURL(ValueReference.of(jiraURL()))
                .proxyURL(ValueReference.of(proxyURL()))
                .graylogURL(ValueReference.of(graylogURL()))
                .credUsername(ValueReference.of(credUsername()))
                .credPassword(ValueReference.of(credPassword()))
                .projectKey(ValueReference.of(projectKey()))
                .issueType(ValueReference.of(issueType()))
                .issueAssigneeName(ValueReference.of(issueAssigneeName()))
                .issuePriority(ValueReference.of(issuePriority()))
                .issueLabels(ValueReference.of(issueLabels()))
                .issueComponents(ValueReference.of(issueComponents()))
                .issueEnvironment(ValueReference.of(issueEnvironment()))
                .issueCustomFields(ValueReference.of(issueCustomFields()))
                .issueSummary(ValueReference.of(issueSummary()))
                .issueDescription(ValueReference.of(issueDescription()))
                .searchGraylogHashField(ValueReference.of(searchGraylogHashField()))
                .searchGraylogHashRegex(ValueReference.of(searchGraylogHashRegex()))
                .searchFilterJQL(ValueReference.of(searchFilterJQL()))
                .duplicateIssueComment(ValueReference.of(duplicateIssueComment()))
                .build();
    }

    private boolean validURL(final String uri) {
        if (StringUtils.isEmpty(uri)) {
            return true;
        }
        try {
            new URI(Objects.requireNonNull(uri));
        } catch (final URISyntaxException ex) {
            return false;
        }
        return true;
    }
}
