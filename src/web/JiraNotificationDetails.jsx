import React from 'react';
import PropTypes from 'prop-types';
import ReadOnlyFormGroup from './components/ReadOnlyFormGroup';

const JiraNotificationDetails = ({ notification }) => {
  return (
    <>
      { notification.config.jira_url
        ? <ReadOnlyFormGroup label="Jira URL" value={notification.config.jira_url} />
        : null}
      { notification.config.proxy_url
        ? <ReadOnlyFormGroup label="Proxy URL" value={notification.config.proxy_url} />
        : null}
      { notification.config.graylog_url
        ? <ReadOnlyFormGroup label="Graylog URL" value={notification.config.graylog_url} />
        : null}
      { notification.config.cred_username
        ? <ReadOnlyFormGroup label="Credentials - username" value={notification.config.cred_username} />
        : null}
      { notification.config.cred_password
        ? <ReadOnlyFormGroup label="Credentials - password" value={notification.config.cred_password} />
        : null}
      { notification.config.project_key
        ? <ReadOnlyFormGroup label="JIRA Project key" value={notification.config.project_key} />
        : null}
      { notification.config.issue_type
        ? <ReadOnlyFormGroup label="JIRA Issue type" value={notification.config.issue_type} />
        : null}
      { notification.config.issue_priority
        ? <ReadOnlyFormGroup label="JIRA Issue priority" value={notification.config.issue_priority} />
        : null}
      { notification.config.issue_labels
        ? <ReadOnlyFormGroup label="JIRA Issue labels" value={notification.config.issue_labels} />
        : null}
      { notification.config.issue_components
        ? <ReadOnlyFormGroup label="JIRA Issue components" value={notification.config.issue_components} />
        : null}
      { notification.config.issue_environment
        ? <ReadOnlyFormGroup label="JIRA Issue environment" value={notification.config.issue_environment} />
        : null}
      { notification.config.issue_custom_fields
        ? <ReadOnlyFormGroup label="JIRA Issue custom fields" value={notification.config.issue_custom_fields} />
        : null}
      { notification.config.issue_summary
        ? <ReadOnlyFormGroup label="JIRA Issue summary" value={notification.config.issue_summary} />
        : null}
      { notification.config.issue_description
        ? <ReadOnlyFormGroup label="JIRA Issue description" value={notification.config.issue_description} />
        : null}
      { notification.config.search_graylog_hash_field
        ? <ReadOnlyFormGroup label="Search for duplicate issues custom field" value={notification.config.search_graylog_hash_field} />
        : null}
      { notification.config.search_graylog_hash_regex
        ? <ReadOnlyFormGroup label="Search for duplicate issues description regex" value={notification.config.search_graylog_hash_regex} />
        : null}
      { notification.config.search_filter_jql
        ? <ReadOnlyFormGroup label="Search filter JQL - duplicate filter" value={notification.config.search_filter_jql} />
        : null}
      { notification.config.graylog_url
        ? <ReadOnlyFormGroup label="Comment inserted in duplicate found issue" value={notification.config.duplicate_issue_comment} />
        : null}
    </>
  );
};

JiraNotificationDetails.propTypes = {
  notification: PropTypes.object.isRequired,
};

export default JiraNotificationDetails;
