package org.graylog.plugins.jira;

import org.graylog.plugins.jira.event.notifications.JiraEventNotificationConfig;
import org.graylog.plugins.jira.event.notifications.JiraEventNotification;
import org.graylog2.plugin.PluginConfigBean;
import org.graylog2.plugin.PluginModule;

import java.util.Collections;
import java.util.Set;

import static org.graylog.plugins.jira.event.notifications.JiraEventNotificationConfig.TYPE_NAME;

public class JiraNotificationModule extends PluginModule {

    @Override
    public Set<? extends PluginConfigBean> getConfigBeans() {
        return Collections.emptySet();
    }

    @Override
    protected void configure() {
        addNotificationType(TYPE_NAME, JiraEventNotificationConfig.class, JiraEventNotification.class,
                JiraEventNotification.Factory.class);
    }
}
