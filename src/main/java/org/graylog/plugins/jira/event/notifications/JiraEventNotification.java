package org.graylog.plugins.jira.event.notifications;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.graylog.events.notifications.EventNotification;
import org.graylog.events.notifications.EventNotificationContext;
import org.graylog.events.notifications.EventNotificationException;
import org.graylog.events.notifications.EventNotificationModelData;
import org.graylog.events.notifications.EventNotificationService;
import org.graylog.events.notifications.PermanentEventNotificationException;
import org.graylog.events.processor.EventDefinitionDto;
import org.graylog.plugins.jira.client.JiraClient;
import org.graylog.plugins.jira.client.JiraClientException;
import org.graylog.scheduler.JobTriggerDto;
import org.graylog2.jackson.TypeReferences;
import org.graylog2.plugin.MessageSummary;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

public class JiraEventNotification implements EventNotification {

    public interface Factory extends EventNotification.Factory<JiraEventNotification> {
        @Override
        JiraEventNotification create();
    }

    private static final String UNKNOWN = "<unknown>";

    private final EventNotificationService notificationCallbackService;
    private final JiraClient jiraClient;
    private final ObjectMapper objectMapper;

    @Inject
    public JiraEventNotification(final EventNotificationService notificationCallbackService,
                                 final JiraClient client,
                                 final ObjectMapper objectMapper) {
        this.notificationCallbackService = notificationCallbackService;
        this.jiraClient = client;
        this.objectMapper = objectMapper;
    }

    @Override
    public void execute(final EventNotificationContext ctx) throws EventNotificationException {
        final JiraEventNotificationConfig config = (JiraEventNotificationConfig) ctx.notificationConfig();
        final ImmutableList<MessageSummary> backlog = notificationCallbackService.getBacklogForEvent(ctx);

        final Map<String, Object> model = getModel(ctx, backlog);
        try {
            jiraClient.createIssue(config, model);
        } catch (final JiraClientException e) {
            throw new PermanentEventNotificationException("JiraEventNotification is triggered but failed sending request", e);
        }
    }

    private Map<String, Object> getModel(final EventNotificationContext ctx,
                                         final ImmutableList<MessageSummary> backlog) {
        final Optional<EventDefinitionDto> definitionDto = ctx.eventDefinition();
        final Optional<JobTriggerDto> jobTriggerDto = ctx.jobTrigger();
        final EventNotificationModelData modelData = EventNotificationModelData.builder()
                .eventDefinitionId(definitionDto.map(EventDefinitionDto::id).orElse(UNKNOWN))
                .eventDefinitionType(definitionDto.map(d -> d.config().type()).orElse(UNKNOWN))
                .eventDefinitionTitle(definitionDto.map(EventDefinitionDto::title).orElse(UNKNOWN))
                .eventDefinitionDescription(definitionDto.map(EventDefinitionDto::description).orElse(UNKNOWN))
                .jobDefinitionId(jobTriggerDto.map(JobTriggerDto::jobDefinitionId).orElse(UNKNOWN))
                .jobTriggerId(jobTriggerDto.map(JobTriggerDto::id).orElse(UNKNOWN))
                .event(ctx.event())
                .backlog(backlog)
                .build();

        return objectMapper.convertValue(modelData, TypeReferences.MAP_STRING_OBJECT);
    }
}
