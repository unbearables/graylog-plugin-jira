# Jira Plugin for Graylog

A Graylog event notification plugin for creating [Jira](https://www.atlassian.com/software/jira) issues.

**Required Graylog version:** 3.1.3 and later

Installation
------------

[Download the plugin](https://github.com/unbearables/graylog-plugin-jira/releases)
and place the `.jar` file in your Graylog plugin directory. The plugin directory
is the `plugins/` folder relative from your `graylog-server` directory by default
and can be configured in your `graylog.conf` file.

Restart `graylog-server` and you are done.

Usage
-----

#### 1. Create Jira user
Create Jira user, that will be used to create issues - this user will be a reporter of these issues.

#### 2. Create custom field for duplicate issue checks (optional)
Create custom field e.g. `Graylog hash` for easy duplicate issue search. Remember to enable this custom field in create issue screen, also allow Jira user (from #1) to fill it and view it.

#### 3. Create Graylog notification
Create Graylog notification and choose `Jira Notification` as Notification type.

#### 4. Configure Jira Notification
Input your Jira configuration and preferences. Here is a screenshot of configuration example.

![Jira configuraiton](https://github.com/unbearables/graylog-plugin-jira/blob/main/img/configuration.png)

#### 5. Create Graylog Event Definitions
Create Graylog Event definition and set Jira Notification you created at #4 as its Notification.

#### 6. Receive notification
You will receive notification message like below.

![Jira notification issue](https://github.com/unbearables/graylog-plugin-jira/blob/main/img/issue.png)

Contribution
------------

1. Fork the repository (https://github.com/unbearables/graylog-plugin-jira/fork)
2. Create your feature branch
3. Commit your changes
4. Rebase your local changes against the main branch
5. Make sure your code can be packaged by `mvn` without any errors
6. Create a new Pull Request

Getting development started
---------------------------

This project is using Maven 3. Requires Java 8 or higher and `yarn`.

* Clone this repository.
* Run `mvn package` to build a JAR file.
* Optional: Run `mvn jdeb:jdeb` and `mvn rpm:rpm` to create a DEB and RPM package respectively.
* Copy generated JAR file in target directory to your Graylog plugin directory.
* Restart the Graylog.

This sets the version numbers, creates a tag and pushes to GitHub. Travis CI will build the release artifacts and upload to GitHub automatically.
