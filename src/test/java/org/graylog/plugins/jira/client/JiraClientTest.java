package org.graylog.plugins.jira.client;

import com.floreysoft.jmte.Engine;
import com.google.common.collect.ImmutableList;

import org.graylog.events.notifications.EventNotificationModelData;
import org.graylog.plugins.jira.event.notifications.JiraEventNotificationConfig;
import org.graylog2.plugin.MessageSummary;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JiraClientTest {

    JiraClient jiraClient = new JiraClient(new Engine());

    @Test
    void createMessageHash_field_success() {
        final String graylogHashField = "msg_hash";
        final String expectedHash = "hash-abc";

        final JiraEventNotificationConfig config = mock();
        when(config.searchGraylogHashJiraField()).thenReturn("field_id=Field name");
        when(config.searchGraylogHashField()).thenReturn(graylogHashField);
        final EventNotificationModelData model = mock();
        final MessageSummary messageSummary = mock();
        when(messageSummary.getField(graylogHashField)).thenReturn(expectedHash);
        when(model.backlog()).thenReturn(ImmutableList.of(messageSummary));

        final String generatedMessageHash = jiraClient.createMessageHash(config, model, "desc");
        assertNotNull(generatedMessageHash);
        assertEquals(expectedHash, generatedMessageHash);
    }

    @Test
    void createMessageHash_field_fail() {
        final String graylogHashField = "msg_hash";

        final JiraEventNotificationConfig config = mock();
        when(config.searchGraylogHashJiraField()).thenReturn("field_id=Field name");
        when(config.searchGraylogHashField()).thenReturn(graylogHashField);
        final EventNotificationModelData model = mock();
        final MessageSummary messageSummary = mock();
        when(messageSummary.getField(graylogHashField)).thenReturn(null);
        when(model.backlog()).thenReturn(ImmutableList.of(messageSummary));

        final String issueDesc = "desc";

        final String generatedMessageHash = jiraClient.createMessageHash(config, model, issueDesc);
        assertNotNull(generatedMessageHash);
        assertEquals(JiraClient.calculateHash(issueDesc), generatedMessageHash);
    }

    @Test
    void createMessageHash_regex_success() {
        final JiraEventNotificationConfig config = mock();
        when(config.searchGraylogHashJiraField()).thenReturn("field_id=Field name");
        when(config.searchGraylogHashField()).thenReturn(null);
        when(config.searchGraylogHashRegex()).thenReturn("(?<=msg=)[\\S\\s]*?(?=,\\w*=)");
        final EventNotificationModelData model = mock();

        final String issueDesc = "msg=abc,field1=def";

        final String generatedMessageHash = jiraClient.createMessageHash(config, model, issueDesc);
        assertNotNull(generatedMessageHash);
        assertEquals(JiraClient.calculateHash("abc"), generatedMessageHash);
    }

    @Test
    void createMessageHash_regex_fail() {
        final JiraEventNotificationConfig config = mock();
        when(config.searchGraylogHashJiraField()).thenReturn("field_id=Field name");
        when(config.searchGraylogHashField()).thenReturn(null);
        when(config.searchGraylogHashRegex()).thenReturn("(?<=msg=)[\\S\\s]*?(?=,\\w*=)");
        final EventNotificationModelData model = mock();

        final String issueDesc = "field1=def,field2=ghi";

        final String generatedMessageHash = jiraClient.createMessageHash(config, model, issueDesc);
        assertNotNull(generatedMessageHash);
        assertEquals(JiraClient.calculateHash(issueDesc), generatedMessageHash);
    }
}
