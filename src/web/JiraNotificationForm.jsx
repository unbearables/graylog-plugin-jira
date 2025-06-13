import React from "react";
import PropTypes from "prop-types";
import lodash from "lodash";
import { FormGroup, ControlLabel, FormControl, HelpBlock } from "react-bootstrap";

class JiraNotificationForm extends React.Component {
  static propTypes = {
    config: PropTypes.object.isRequired,
    validation: PropTypes.object.isRequired,
    onChange: PropTypes.func.isRequired
  };

  propagateChange = (key, value) => {
    const { config, onChange } = this.props;
    const nextConfig = lodash.cloneDeep(config);
    nextConfig[key] = value;
    onChange(nextConfig);
  };

  handleChange = event => {
    this.propagateChange(event.target.name, event.target.value);
  };

  getValidationState = (validationErrors, fieldId) => {
    if (validationErrors.includes(fieldId)) {
      return "error";
    }
    return null;
  };

  render() {
    let validationErrors = [];
    const { config, validation } = this.props;
    if (validation?.errors) {
      validationErrors = Object.keys(validation.errors);
    }
    return (
      <React.Fragment>
        <h3>General settings</h3>
        <br/>
        <FormGroup
          controlId="notification-jira-url"
          validationState={this.getValidationState(validationErrors, "jira_url")}
        >
          <ControlLabel>Jira URL</ControlLabel>
          <FormControl
            name="jira_url"
            type="text"
            value={config.jira_url || ""}
            onChange={this.handleChange}
            required
          />
          <HelpBlock>Jira base URL e.g. company.jira.com</HelpBlock>
        </FormGroup>
        <FormGroup
          controlId="notification-proxy-url"
          validationState={this.getValidationState(validationErrors, "proxy_url")}
        >
          <ControlLabel>Proxy URL <small class="text-muted">(Optional)</small></ControlLabel>
          <FormControl
            name="proxy_url"
            type="text"
            value={config.proxy_url || ""}
            onChange={this.handleChange}
          />
          <HelpBlock>Proxy URL in the following format "http(s)://HOST:PORT</HelpBlock>
        </FormGroup>
        <FormGroup
          controlId="notification-graylog-url"
          validationState={this.getValidationState(validationErrors, "graylog_url")}
        >
          <ControlLabel>Graylog URL <small class="text-muted">(Optional)</small></ControlLabel>
          <FormControl
            name="graylog_url"
            type="text"
            value={config.graylog_url || ""}
            onChange={this.handleChange}
          />
          <HelpBlock>Graylog URL that can be used in summary or description as 'graylog_url'</HelpBlock>
        </FormGroup>

        <h3>Credentials</h3>
        <FormGroup
          controlId="notification-cred-username"
          validationState={this.getValidationState(validationErrors, "cred_username")}
        >
          <ControlLabel>Credentials - username</ControlLabel>
          <FormControl
            name="cred_username"
            type="text"
            value={config.cred_username || ""}
            onChange={this.handleChange}
            required
          />
        </FormGroup>
        <FormGroup
          controlId="notification-cred-password"
          validationState={this.getValidationState(validationErrors, "cred_password")}
        >
          <ControlLabel>Credentials - password</ControlLabel>
          <FormControl
            name="cred_password"
            type="password"
            value={config.cred_password || ""}
            onChange={this.handleChange}
            required
          />
        </FormGroup>

        <h3>JIRA issue creation</h3>
        <br/>
        <FormGroup
          controlId="notification-project-key"
          validationState={this.getValidationState(validationErrors, "project_key")}
        >
          <ControlLabel>JIRA Project key</ControlLabel>
          <FormControl
            name="project_key"
            type="text"
            value={config.project_key || ""}
            onChange={this.handleChange}
            required
          />
          <HelpBlock>Project key e.g. ABC</HelpBlock>
        </FormGroup>
        <FormGroup
          controlId="notification-issue-type"
          validationState={this.getValidationState(validationErrors, "issue_type")}
        >
          <ControlLabel>JIRA Issue type</ControlLabel>
          <FormControl
            name="issue_type"
            type="text"
            value={config.issue_type || ""}
            onChange={this.handleChange}
            required
          />
          <HelpBlock>Issue type e.g. Bug (needs to exist in your project)</HelpBlock>
        </FormGroup>
        <FormGroup
          controlId="notification-issue-assignee-name"
          validationState={this.getValidationState(validationErrors, "issue_assignee_name")}
        >
          <ControlLabel>JIRA Issue assignee username <small class="text-muted">(Optional)</small></ControlLabel>
          <FormControl
            name="issue_assignee_name"
            type="text"
            value={config.issue_assignee_name || ""}
            onChange={this.handleChange}
          />
          <HelpBlock>See username in Jira profile</HelpBlock>
        </FormGroup>
        <FormGroup
          controlId="notification-issue-priority"
          validationState={this.getValidationState(validationErrors, "issue_priority")}
        >
          <ControlLabel>JIRA Issue priority <small class="text-muted">(Optional)</small></ControlLabel>
          <FormControl
            name="issue_priority"
            type="text"
            value={config.issue_priority || ""}
            onChange={this.handleChange}
          />
          <HelpBlock>Issue priority e.g. High (needs to exist in your project)</HelpBlock>
        </FormGroup>
        <FormGroup
          controlId="notification-issue-labels"
          validationState={this.getValidationState(validationErrors, "issue_labels")}
        >
          <ControlLabel>JIRA Issue labels <small class="text-muted">(Optional)</small></ControlLabel>
          <FormControl
            name="issue_labels"
            type="text"
            value={config.issue_labels || ""}
            onChange={this.handleChange}
          />
          <HelpBlock>Issue labels (delimited with ';')</HelpBlock>
        </FormGroup>
        <FormGroup
          controlId="notification-issue-components"
          validationState={this.getValidationState(validationErrors, "issue_components")}
        >
          <ControlLabel>JIRA Issue components <small class="text-muted">(Optional)</small></ControlLabel>
          <FormControl
            name="issue_components"
            type="text"
            value={config.issue_components || ""}
            onChange={this.handleChange}
          />
          <HelpBlock>Issue components (delimited with ';')</HelpBlock>
        </FormGroup>
        <FormGroup
          controlId="notification-issue-environment"
          validationState={this.getValidationState(validationErrors, "issue_environment")}
        >
          <ControlLabel>JIRA Issue environment <small class="text-muted">(Optional)</small></ControlLabel>
          <FormControl
            name="issue_environment"
            type="text"
            value={config.issue_environment || ""}
            onChange={this.handleChange}
          />
          <HelpBlock>Issue environment</HelpBlock>
        </FormGroup>
        <FormGroup
          controlId="notification-issue-custom-fields"
          validationState={this.getValidationState(validationErrors, "issue_custom_fields")}
        >
          <ControlLabel>JIRA Issue custom fields <small class="text-muted">(Optional)</small></ControlLabel>
          <FormControl
            name="issue_custom_fields"
            type="text"
            value={config.issue_custom_fields || ""}
            onChange={this.handleChange}
          />
          <HelpBlock>Issue custom fields (key=value format delimited with ',') example 'custom1=dog,custom2=bear'</HelpBlock>
        </FormGroup>
        <FormGroup
          controlId="notification-issue-summary"
          validationState={this.getValidationState(validationErrors, "issue_summary")}
        >
          <ControlLabel>JIRA Issue summary</ControlLabel>
          <FormControl
            name="issue_summary"
            type="text"
            value={config.issue_summary || ""}
            onChange={this.handleChange}
            required
          />
          <HelpBlock>Issue summary (title) - event and message data is accessible</HelpBlock>
        </FormGroup>
        <FormGroup
          controlId="notification-issue-description"
          validationState={this.getValidationState(validationErrors, "issue_description")}
        >
          <ControlLabel>JIRA Issue description</ControlLabel>
          <FormControl
            name="issue_description"
            componentClass="textarea"
            value={config.issue_description || ""}
            onChange={this.handleChange}
            required
          />
          <HelpBlock>Issue description - event and message data is accessible, basic Markdown is supported</HelpBlock>
        </FormGroup>

        <h3>Duplicate issue handling</h3>
        <br/>
        <FormGroup
          controlId="notification-search-graylog-hash-field"
          validationState={this.getValidationState(validationErrors, "search_graylog_hash_field")}
        >
          <ControlLabel>Search for duplicate issues custom field <small class="text-muted">(Optional)</small></ControlLabel>
          <FormControl
            name="search_graylog_hash_field"
            type="text"
            value={config.search_graylog_hash_field || ""}
            onChange={this.handleChange}
          />
          <HelpBlock>Pair value custom field id to it's name in GUI e.g. 'customfield_123=Graylog hash'. Leave blank to turn off duplicate searching.</HelpBlock>
        </FormGroup>
        <FormGroup
          controlId="notification-search-graylog-hash-regex"
          validationState={this.getValidationState(validationErrors, "search_graylog_hash_regex")}
        >
          <ControlLabel>Search for duplicate issues description regex <small class="text-muted">(Optional)</small></ControlLabel>
          <FormControl
            name="search_graylog_hash_regex"
            type="text"
            value={config.search_graylog_hash_regex || ""}
            onChange={this.handleChange}
          />
          <HelpBlock>Regex for extracting part of issue description, which can be used to detect duplicate issues. If absent and graylog hash field is filled, then whole description will be used.</HelpBlock>
        </FormGroup>
        <FormGroup
          controlId="notification-search-filter-jql"
          validationState={this.getValidationState(validationErrors, "search_filter_jql")}
        >
          <ControlLabel>Search filter JQL - duplicate filter <small class="text-muted">(Optional)</small></ControlLabel>
          <FormControl
            name="search_filter_jql"
            type="text"
            value={config.search_filter_jql || ""}
            onChange={this.handleChange}
          />
          <HelpBlock>Search filter JQL - duplicate filter e.g. 'AND resolution = Unresolved'</HelpBlock>
        </FormGroup>
        <FormGroup
          controlId="notification-duplicate-issue-comment"
          validationState={this.getValidationState(validationErrors, "duplicate_issue_comment")}
        >
          <ControlLabel>Comment inserted in duplicate found issue <small class="text-muted">(Optional)</small></ControlLabel>
          <FormControl
            name="duplicate_issue_comment"
            type="text"
            value={config.duplicate_issue_comment || ""}
            onChange={this.handleChange}
          />
          <HelpBlock>Comment to add to found duplicate issue. Event and message fields are accessible (just like in issue description). Leave blank to disable commenting.</HelpBlock>
        </FormGroup>
      </React.Fragment>
    );
  }
}

export default JiraNotificationForm;
