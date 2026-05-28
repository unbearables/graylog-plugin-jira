package org.graylog.plugins.jira.event.notifications;

import com.google.common.collect.ImmutableList;

import org.graylog.events.notifications.EventNotification;
import org.graylog.events.notifications.EventNotificationContext;
import org.graylog.events.notifications.EventNotificationException;
import org.graylog.events.notifications.EventNotificationModelData;
import org.graylog.events.notifications.EventNotificationService;
import org.graylog.events.notifications.PermanentEventNotificationException;
import org.graylog.plugins.jira.client.JiraClient;
import org.graylog.plugins.jira.client.JiraClientException;
import org.graylog2.plugin.MessageSummary;

import jakarta.inject.Inject;

public class JiraEventNotification implements EventNotification {

    public interface Factory extends EventNotification.Factory<JiraEventNotification> {
        @Override
        JiraEventNotification create();
    }

    private final EventNotificationService notificationCallbackService;
    private final JiraClient jiraClient;

    @Inject
    public JiraEventNotification(final EventNotificationService notificationCallbackService,
                                 final JiraClient client) {
        this.notificationCallbackService = notificationCallbackService;
        this.jiraClient = client;
    }

    @Override
    public void execute(final EventNotificationContext ctx) throws EventNotificationException {
        final JiraEventNotificationConfig config = (JiraEventNotificationConfig) ctx.notificationConfig();
        final ImmutableList<MessageSummary> backlog = notificationCallbackService.getBacklogForEvent(ctx);

        final EventNotificationModelData model = EventNotificationModelData.of(ctx, backlog);
        try {
            jiraClient.createIssue(config, model);
        } catch (final JiraClientException e) {
            throw new PermanentEventNotificationException("JiraEventNotification is triggered but failed sending request", e);
        }
    }
}
