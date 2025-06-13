package org.graylog.plugins.jira;

import org.graylog.plugins.jira.event.notifications.JiraEventNotification;
import org.graylog.plugins.jira.event.notifications.JiraEventNotificationConfig;
import org.graylog.plugins.jira.event.notifications.JiraEventNotificationConfigEntity;
import org.graylog2.plugin.PluginModule;

public class JiraNotificationModule extends PluginModule {

    @Override
    protected void configure() {
        addNotificationType(
            JiraEventNotificationConfig.TYPE_NAME,
            JiraEventNotificationConfig.class,
            JiraEventNotification.class,
            JiraEventNotification.Factory.class,
            JiraEventNotificationConfigEntity.TYPE_NAME,
            JiraEventNotificationConfigEntity.class
        );
    }
}
