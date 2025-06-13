import { PluginManifest, PluginStore } from 'graylog-web-plugin/plugin';

import JiraNotificationDetails from 'JiraNotificationDetails';
import JiraNotificationForm from 'JiraNotificationForm';
import JiraNotificationSummary from 'JiraNotificationSummary';
import { DEFAULT_CONFIG } from 'JiraNotificationConfig';

import packageJson from '../../package.json';

PluginStore.register(
  new PluginManifest(packageJson,
    {
        eventNotificationTypes: [
            {
                type: 'jira-notification',
                displayName: 'Jira Notification',
                formComponent: JiraNotificationForm,
                summaryComponent: JiraNotificationSummary,
                detailsComponent: JiraNotificationDetails,
                defaultConfig: DEFAULT_CONFIG
            }
        ]
    }
  )
);
