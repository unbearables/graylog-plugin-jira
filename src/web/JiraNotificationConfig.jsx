const DEFAULT_ISSUE_SUMMARY = "Graylog log error - ${event.id}"
const DEFAULT_ISSUE_DESCRIPTION = `--- [Event Definition] ---
*ID:* \${event_definition_id}
*Type:* \${event_definition_type}
*Title:* \${event_definition_title}
*Description:* \${event_definition_description}
--- [Event] ---
*Event:* \${event}
--- [Event Detail] ---
*Timestamp:* \${event.timestamp}
*Message:* \${event.message}
*Source:* \${event.source}
*Key:* \${event.key}
*Priority:* \${event.priority}
*Alert:* \${event.alert}
*Timestamp Processing:* \${event.timestamp}
*TimeRange Start:* \${event.timerange_start}
*TimeRange End:* \${event.timerange_end}
\${if event.fields}
*Fields:*
\${foreach event.fields field} \${field.key}: \${field.value}
\${end}
\${end}
\${if backlog}
--- [Backlog] ---
*Messages:*
\${foreach backlog message}
Graylog link: \${graylog_url}/messages/\${message.index}/\${message.id}
\`\`\`
\${message}
\`\`\`
\${end}
\${end}`;

export const DEFAULT_CONFIG = {
    issue_summary: DEFAULT_ISSUE_SUMMARY,
    issue_description: DEFAULT_ISSUE_DESCRIPTION
};
