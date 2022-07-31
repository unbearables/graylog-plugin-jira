import React from "react";
import PropTypes from "prop-types";

import CommonNotificationSummary from "./CommonNotificationSummary";

class JiraNotificationSummary extends React.Component {
  static propTypes = {
    type: PropTypes.string.isRequired,
    notification: PropTypes.object,
    definitionNotification: PropTypes.object.isRequired
  };

  static defaultProps = {
    notification: {}
  };

  render() {
    const { notification } = this.props;
    return (
      <CommonNotificationSummary {...this.props}>
        <React.Fragment>
          <tr>
            <td>JIRA URL</td>
            <td>{notification.config.jira_url}</td>
          </tr>
          <tr>
            <td>Proxy URL</td>
            <td>{notification.config.proxy_url}</td>
          </tr>
          <tr>
            <td>Graylog URL</td>
            <td>{notification.config.graylog_url}</td>
          </tr>
          <tr>
            <td>Credentials username</td>
            <td>{notification.config.cred_username}</td>
          </tr>
          <tr>
            <td>Credentials password</td>
            <td>{notification.config.cred_password}</td>
          </tr>

          <tr>
            <td>Project key</td>
            <td>{notification.config.project_key}</td>
          </tr>
          <tr>
            <td>Issue type</td>
            <td>{notification.config.issue_type}</td>
          </tr>
          <tr>
            <td>Issue priority</td>
            <td>{notification.config.issue_priority}</td>
          </tr>
          <tr>
            <td>Issue labels</td>
            <td>{notification.config.issue_labels}</td>
          </tr>
          <tr>
            <td>Issue components</td>
            <td>{notification.config.issue_components}</td>
          </tr>
          <tr>
            <td>Issue environment</td>
            <td>{notification.config.issue_environment}</td>
          </tr>
          <tr>
            <td>Issue custom fields</td>
            <td>{notification.config.issue_custom_fields}</td>
          </tr>
          <tr>
            <td>Issue summary</td>
            <td>{notification.config.issue_summary}</td>
          </tr>
          <tr>
            <td>Issue description</td>
            <td>{notification.config.issue_description}</td>
          </tr>
          <tr>
            <td>Search for duplicate issues custom field</td>
            <td>{notification.config.search_graylog_hash_field}</td>
          </tr>
          <tr>
            <td>JQL filter for duplicate issues search</td>
            <td>{notification.config.search_filter_jql}</td>
          </tr>
        </React.Fragment>
      </CommonNotificationSummary>
    );
  }
}

export default JiraNotificationSummary;
