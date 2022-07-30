package org.graylog.plugins.jira.client;

public class JiraClientException extends RuntimeException {

  private static final long serialVersionUID = -2367942499007524159L;

  public JiraClientException() {
    super();
  }

  public JiraClientException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public JiraClientException(final String message) {
    super(message);
  }
}
