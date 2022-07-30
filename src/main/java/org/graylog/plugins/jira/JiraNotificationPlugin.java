package org.graylog.plugins.jira;

import org.graylog2.plugin.Plugin;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.PluginModule;

import java.util.Collection;
import java.util.Collections;

public class JiraNotificationPlugin implements Plugin {
    @Override
    public PluginMetaData metadata() {
        return new JiraNotificationMetaData();
    }

    @Override
    public Collection<PluginModule> modules () {
        return Collections.singletonList(new JiraNotificationModule());
    }
}
