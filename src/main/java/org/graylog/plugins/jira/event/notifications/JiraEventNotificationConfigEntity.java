package org.graylog.plugins.jira.event.notifications;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import org.graylog.events.contentpack.entities.EventNotificationConfigEntity;
import org.graylog.events.notifications.EventNotificationConfig;
import org.graylog2.contentpacks.model.entities.EntityDescriptor;
import org.graylog2.contentpacks.model.entities.references.ValueReference;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@AutoValue
@JsonTypeName(JiraEventNotificationConfigEntity.TYPE_NAME)
@JsonDeserialize(builder = JiraEventNotificationConfigEntity.Builder.class)
public abstract class JiraEventNotificationConfigEntity implements EventNotificationConfigEntity {

    public static final String TYPE_NAME = "jira-notification";

    @JsonProperty(JiraEventNotificationConfig.FIELD_JIRA_URL)
    @NotBlank
    public abstract ValueReference jiraURL();

    @JsonProperty(JiraEventNotificationConfig.FIELD_PROXY_URL)
    @NotBlank
    public abstract ValueReference proxyURL();

    @JsonProperty(JiraEventNotificationConfig.FIELD_GRAYLOG_URL)
    public abstract ValueReference graylogURL();

    @JsonProperty(JiraEventNotificationConfig.FIELD_CRED_USERNAME)
    public abstract ValueReference credUsername();

    @JsonProperty(JiraEventNotificationConfig.FIELD_CRED_PWD)
    public abstract ValueReference credPassword();

    @JsonProperty(JiraEventNotificationConfig.FIELD_PROJECT_KEY)
    public abstract ValueReference projectKey();

    @JsonProperty(JiraEventNotificationConfig.FIELD_ISSUE_TYPE)
    public abstract ValueReference issueType();

    @JsonProperty(JiraEventNotificationConfig.FIELD_ISSUE_PRIORITY)
    public abstract ValueReference issuePriority();

    @JsonProperty(JiraEventNotificationConfig.FIELD_ISSUE_LABELS)
    public abstract ValueReference issueLabels();

    @JsonProperty(JiraEventNotificationConfig.FIELD_ISSUE_COMPONENTS)
    public abstract ValueReference issueComponents();

    @JsonProperty(JiraEventNotificationConfig.FIELD_ISSUE_ENVIRONMENT)
    public abstract ValueReference issueEnvironment();

    @JsonProperty(JiraEventNotificationConfig.FIELD_ISSUE_CUSTOM_FIELDS)
    public abstract ValueReference issueCustomFields();

    @JsonProperty(JiraEventNotificationConfig.FIELD_ISSUE_SUMMARY)
    public abstract ValueReference issueSummary();

    @JsonProperty(JiraEventNotificationConfig.FIELD_ISSUE_DESCRIPTION)
    public abstract ValueReference issueDescription();

    @JsonProperty(JiraEventNotificationConfig.FIELD_SEARCH_GRAYLOG_HASH_FIELD)
    public abstract ValueReference searchGraylogHashField();

    @JsonProperty(JiraEventNotificationConfig.FIELD_SEARCH_GRAYLOG_HASH_REGEX)
    public abstract ValueReference searchGraylogHashRegex();

    @JsonProperty(JiraEventNotificationConfig.FIELD_SEARCH_FILTER_JQL)
    public abstract ValueReference searchFilterJQL();

    @JsonProperty(JiraEventNotificationConfig.FIELD_DUPLICATE_ISSUE_COMMENT)
    public abstract ValueReference duplicateIssueComment();

    public static Builder builder() {
        return Builder.create();
    }

    public abstract Builder toBuilder();

    @JsonIgnoreProperties(ignoreUnknown = true)
    @AutoValue.Builder
    public static abstract class Builder implements EventNotificationConfigEntity.Builder<Builder> {

        @JsonCreator
        public static Builder create() {
            return new AutoValue_JiraEventNotificationConfigEntity.Builder().type(TYPE_NAME);
        }

        @JsonProperty(JiraEventNotificationConfig.FIELD_JIRA_URL)
        public abstract Builder jiraURL(ValueReference jiraURL);

        @JsonProperty(JiraEventNotificationConfig.FIELD_PROXY_URL)
        public abstract Builder proxyURL(ValueReference proxyURL);

        @JsonProperty(JiraEventNotificationConfig.FIELD_GRAYLOG_URL)
        public abstract Builder graylogURL(ValueReference graylogURL);

        @JsonProperty(JiraEventNotificationConfig.FIELD_CRED_USERNAME)
        public abstract Builder credUsername(ValueReference credUsername);

        @JsonProperty(JiraEventNotificationConfig.FIELD_CRED_PWD)
        public abstract Builder credPassword(ValueReference credPassword);

        @JsonProperty(JiraEventNotificationConfig.FIELD_PROJECT_KEY)
        public abstract Builder projectKey(ValueReference projectKey);

        @JsonProperty(JiraEventNotificationConfig.FIELD_ISSUE_TYPE)
        public abstract Builder issueType(ValueReference issueType);

        @JsonProperty(JiraEventNotificationConfig.FIELD_ISSUE_PRIORITY)
        public abstract Builder issuePriority(ValueReference issuePriority);

        @JsonProperty(JiraEventNotificationConfig.FIELD_ISSUE_LABELS)
        public abstract Builder issueLabels(ValueReference issueLabels);

        @JsonProperty(JiraEventNotificationConfig.FIELD_ISSUE_COMPONENTS)
        public abstract Builder issueComponents(ValueReference issueComponents);

        @JsonProperty(JiraEventNotificationConfig.FIELD_ISSUE_ENVIRONMENT)
        public abstract Builder issueEnvironment(ValueReference issueEnvironment);

        @JsonProperty(JiraEventNotificationConfig.FIELD_ISSUE_CUSTOM_FIELDS)
        public abstract Builder issueCustomFields(ValueReference issueCustomFields);

        @JsonProperty(JiraEventNotificationConfig.FIELD_ISSUE_SUMMARY)
        public abstract Builder issueSummary(ValueReference issueSummary);

        @JsonProperty(JiraEventNotificationConfig.FIELD_ISSUE_DESCRIPTION)
        public abstract Builder issueDescription(ValueReference issueDescription);

        @JsonProperty(JiraEventNotificationConfig.FIELD_SEARCH_GRAYLOG_HASH_FIELD)
        public abstract Builder searchGraylogHashField(ValueReference searchGraylogHashFieldName);

        @JsonProperty(JiraEventNotificationConfig.FIELD_SEARCH_GRAYLOG_HASH_REGEX)
        public abstract Builder searchGraylogHashRegex(ValueReference searchGraylogHashRegex);

        @JsonProperty(JiraEventNotificationConfig.FIELD_SEARCH_FILTER_JQL)
        public abstract Builder searchFilterJQL(ValueReference searchFilterJQL);

        @JsonProperty(JiraEventNotificationConfig.FIELD_DUPLICATE_ISSUE_COMMENT)
        public abstract Builder duplicateIssueComment(ValueReference duplicateIssueComment);

        public abstract JiraEventNotificationConfigEntity build();
    }

    @Override
    public EventNotificationConfig toNativeEntity(Map<String, ValueReference> parameters,
                                                  Map<EntityDescriptor, Object> nativeEntities) {
        return JiraEventNotificationConfig.builder()
                .jiraURL(jiraURL().asString(parameters))
                .proxyURL(proxyURL().asString(parameters))
                .graylogURL(graylogURL().asString(parameters))
                .credUsername(credUsername().asString(parameters))
                .credPassword(credPassword().asString(parameters))
                .projectKey(projectKey().asString(parameters))
                .issueType(issueType().asString(parameters))
                .issuePriority(issuePriority().asString(parameters))
                .issueLabels(issueLabels().asString(parameters))
                .issueComponents(issueComponents().asString(parameters))
                .issueEnvironment(issueEnvironment().asString(parameters))
                .issueCustomFields(issueCustomFields().asString(parameters))
                .issueSummary(issueSummary().asString(parameters))
                .issueDescription(issueDescription().asString(parameters))
                .searchGraylogHashField(searchGraylogHashField().asString(parameters))
                .searchGraylogHashRegex(searchGraylogHashRegex().asString(parameters))
                .searchFilterJQL(searchFilterJQL().asString(parameters))
                .duplicateIssueComment(duplicateIssueComment().asString(parameters))
                .build();
    }
}
