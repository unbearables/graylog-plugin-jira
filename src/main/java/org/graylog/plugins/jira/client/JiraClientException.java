package org.graylog.plugins.jira.client;

import java.io.Serial;

public class JiraClientException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -2367942499007524159L;

    public JiraClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JiraClientException(final String message) {
        super(message);
    }
}
