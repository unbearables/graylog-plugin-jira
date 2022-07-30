import { PluginManifest, PluginStore } from "graylog-web-plugin/plugin";

import JiraNotificationForm from "./JiraNotificationForm";
import JiraNotificationSummary from "./JiraNotificationSummary";

PluginStore.register(
  new PluginManifest(
    {},
    {
      eventNotificationTypes: [
        {
          type: "jira-notification",
          displayName: "Jira Notification",
          formComponent: JiraNotificationForm,
          summaryComponent: JiraNotificationSummary,
          defaultConfig: JiraNotificationForm.defaultConfig
        }
      ]
    }
  )
);
