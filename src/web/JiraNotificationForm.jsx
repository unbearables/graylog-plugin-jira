import React from "react";
import PropTypes from "prop-types";
import lodash from "lodash";
import { Input } from "components/bootstrap";
import FormsUtils from "util/FormsUtils";

const DEFAULT_ISSUE_SUMMARY = "Graylog log error - ${event.id}"
const DEFAULT_ISSUE_DESCRIPTION = `--- [Event Definition] ---------------------------
*ID:**                  \${event_definition_id}
*Type:*                 \${event_definition_type}
*Title:*                \${event_definition_title}  t
*Description:*          \${event_definition_description}
--- [Event] --------------------------------------
*Event:*                \${event}
--- [Event Detail] -------------------------------
*Timestamp:*            \${event.timestamp}
*Message:*              \${event.message}
*Source:*               \${event.source}
*Key:*                  \${event.key}
*Priority:*             \${event.priority}
*Alert:*                \${event.alert}
*Timestamp Processing:* \${event.timestamp}
*TimeRange Start:*      \${event.timerange_start}
*TimeRange End:*        \${event.timerange_end}
\${if event.fields}
*Fields:*
\${foreach event.fields field}  \${field.key}: \${field.value}  
\${end}
\${end}
\${if backlog}
--- [Backlog] ------------------------------------
*Messages:*
\${foreach backlog message}
Graylog link: \${graylog_url}/messages/graylog_0/\${message.id}
\`\`\`
\${message}  
\`\`\`
\${end}
\${end}`;

class JiraNotificationForm extends React.Component {
  static propTypes = {
    config: PropTypes.object.isRequired,
    validation: PropTypes.object.isRequired,
    onChange: PropTypes.func.isRequired
  };

  static defaultConfig = {
    issue_summary: DEFAULT_ISSUE_SUMMARY,
    issue_description: DEFAULT_ISSUE_DESCRIPTION
  };

  propagateChange = (key, value) => {
    const { config, onChange } = this.props;
    const nextConfig = lodash.cloneDeep(config);
    nextConfig[key] = value;
    onChange(nextConfig);
  };

  handleChange = event => {
    const { name } = event.target;
    this.propagateChange(name, FormsUtils.getValueFromInput(event.target));
  };

  render() {
    const { config, validation } = this.props;
    return (
      <React.Fragment>
        <Input
          id="notification-jira-url"
          name="jira_url"
          label="Jira URL"
          type="text"
          bsStyle={validation.errors.jira_url ? "error" : null}
          help={lodash.get(
            validation,
            "errors.jira_url[0]",
            "Jira base URL e.g. company.jira.com"
          )}
          value={config.jira_url || ""}
          onChange={this.handleChange}
          required
        />
        <Input
          id="notification-proxy-url"
          name="proxy_url"
          label="Proxy URL"
          type="text"
          bsStyle={validation.errors.proxy_url ? "error" : null}
          help={lodash.get(
            validation,
            "errors.proxy_url[0]",
            'Proxy URL in the following format "http(s)://${HOST}:${PORT}"'
          )}
          value={config.proxy_url || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-graylog-url"
          name="graylog_url"
          label="Graylog URL"
          type="text"
          bsStyle={validation.errors.graylog_url ? "error" : null}
          help={lodash.get(
            validation,
            "errors.graylog_url[0]",
            "Graylog URL that can be used in summary or description as 'graylog_url'"
          )}
          value={config.graylog_url || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-cred-username"
          name="cred_username"
          label="Credentials - username"
          type="text"
          bsStyle={validation.errors.cred_username ? "error" : null}
          help={lodash.get(
            validation,
            "errors.cred_username[0]",
            "User credentials username"
          )}
          value={config.cred_username || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-cred-password"
          name="cred_password"
          label="Credentials - password"
          type="password"
          bsStyle={validation.errors.cred_password ? "error" : null}
          help={lodash.get(
            validation,
            "errors.cred_password[0]",
            "User credentials password"
          )}
          value={config.cred_password || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-project-key"
          name="project_key"
          label="JIRA Project key"
          type="text"
          bsStyle={validation.errors.project_key ? "error" : null}
          help={lodash.get(
            validation,
            "errors.project_key[0]",
            "Project key e.g. ABC"
          )}
          value={config.project_key || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-issue-type"
          name="issue_type"
          label="JIRA Issue type"
          type="text"
          bsStyle={validation.errors.issue_type ? "error" : null}
          help={lodash.get(
            validation,
            "errors.issue_type[0]",
            "Issue type e.g. Bug"
          )}
          value={config.issue_type || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-issue-priority"
          name="issue_priority"
          label="JIRA Issue priority"
          type="text"
          bsStyle={validation.errors.issue_priority ? "error" : null}
          help={lodash.get(
            validation,
            "errors.issue_priority[0]",
            "Issue priority e.g. High"
          )}
          value={config.issue_priority || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-issue-labels"
          name="issue_labels"
          label="JIRA Issue labels"
          type="text"
          bsStyle={validation.errors.issue_labels ? "error" : null}
          help={lodash.get(
            validation,
            "errors.issue_labels[0]",
            "Issue labels (delimited with ';')"
          )}
          value={config.issue_labels || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-issue-components"
          name="issue_components"
          label="JIRA Issue components"
          type="text"
          bsStyle={validation.errors.issue_components ? "error" : null}
          help={lodash.get(
            validation,
            "errors.issue_components[0]",
            "Issue components (delimited with ';')"
          )}
          value={config.issue_components || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-issue-environment"
          name="issue_environment"
          label="JIRA Issue environment"
          type="text"
          bsStyle={validation.errors.issue_environment ? "error" : null}
          help={lodash.get(
            validation,
            "errors.issue_environment[0]",
            "Issue environment"
          )}
          value={config.issue_environment || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-issue-custom-fields"
          name="issue_custom_fields"
          label="JIRA Issue custom fields"
          type="text"
          bsStyle={validation.errors.issue_custom_fields ? "error" : null}
          help={lodash.get(
            validation,
            "errors.issue_custom_fields[0]",
            "Issue custom fields (key=value format delimited with ',') example 'custom1=dog,custom2=bear'"
          )}
          value={config.issue_custom_fields || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-issue-summary"
          name="issue_summary"
          label="JIRA Issue summary"
          type="text"
          bsStyle={validation.errors.issue_summary ? "error" : null}
          help={lodash.get(
            validation,
            "errors.issue_summary[0]",
            "Issue summary (title) - event and message data is accessible"
          )}
          value={config.issue_summary || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-issue-description"
          name="issue_description"
          label="JIRA Issue description"
          type="textarea"
          bsStyle={validation.errors.issue_description ? "error" : null}
          help={lodash.get(
            validation,
            "errors.issue_description[0]",
            "Issue description - event and message data is accessible, basic Markdown is supported"
          )}
          value={config.issue_description || ""}
          rows={15}
          onChange={this.handleChange}
        />
        <Input
          id="notification-search-graylog-hash-field"
          name="search_graylog_hash_field"
          label="Search for duplicate issues custom field"
          type="text"
          bsStyle={validation.errors.search_graylog_hash_field ? "error" : null}
          help={lodash.get(
            validation,
            "errors.search_graylog_hash_field[0]",
            "Pair value custom field id to it's name in GUI e.g. 'customfield_123=Graylog hash'. Leave blank to turn off searching."
          )}
          value={config.search_graylog_hash_field || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-search-filter-jql"
          name="search_filter_jql"
          label="Search filter JQL - duplicate filter"
          type="text"
          bsStyle={validation.errors.search_filter_jql ? "error" : null}
          help={lodash.get(
            validation,
            "errors.search_filter_jql[0]",
            "Search filter JQL - duplicate filter e.g. 'AND resolution = Unresolved'"
          )}
          value={config.search_filter_jql || ""}
          onChange={this.handleChange}
        />
      </React.Fragment>
    );
  }
}

export default JiraNotificationForm;
