package org.graylog.plugins.jira;

import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.Version;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

public class JiraNotificationMetaData implements PluginMetaData {

    private static final String PLUGIN_PROPERTIES = "org.graylog.plugins.graylog-plugin-jira/graylog-plugin.properties";

    @Override
    public String getUniqueId() {
        return "org.graylog.plugins.jira.JiraNotificationPlugin";
    }

    @Override
    public String getName() {
        return "Jira Notification";
    }

    @Override
    public String getAuthor() {
        return "David Horak";
    }

    @Override
    public URI getURL() {
        return URI.create("https://github.com/unbearables/graylog-plugin-jira");
    }

    @Override
    public Version getVersion() {
        return Version.fromPluginProperties(getClass(), PLUGIN_PROPERTIES, "version", Version.from(0, 0, 0, "unknown"));
    }

    @Override
    public String getDescription() {
        return "Jira plugin";
    }

    @Override
    public Version getRequiredVersion() {
        return Version.fromPluginProperties(getClass(), PLUGIN_PROPERTIES, "graylog.version", Version.from(0, 0, 0, "unknown"));
    }

    @Override
    public Set<ServerStatus.Capability> getRequiredCapabilities() {
        return Collections.emptySet();
    }
}
