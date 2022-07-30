package org.graylog.plugins.jira.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JiraIssueTest {

    @Test
    void toJsonString_success() throws IOException {
        final Set<String> labels = new HashSet<>();
        labels.add("label");
        final Set<String> components = new HashSet<>();
        components.add("component");
        final Map<String, String> customFields = new HashMap<>();
        customFields.put("customfield_1", "custom");
        final JiraIssue ji = new JiraIssue("GRAYLOG","summary", "desc",
                "bug", "high", labels, components, "test",
                "123", customFields);
        final String expected = "{"
                + "\"fields\": {"
                + "\"project\":{\"key\":\"GRAYLOG\"},"
                + "\"summary\":\"summary\","
                + "\"description\":\"desc\","
                + "\"issueType\":{\"name\":\"bug\"},"
                + "\"priority\":{\"name\":\"high\"},"
                + "\"labels\":[\"label\"],"
                + "\"components\":["
                + "  {\"name\":\"component\"}"
                + "],"
                + "\"environment\":\"test\","
                + "\"customfield_123\":\"" + JiraIssue.createGraylogHash("desc") + "\","
                + "\"customfield_1\":\"custom\""
                + "}"
                + "}";

        assertJSON(expected, ji.toJsonString());
    }

    private void assertJSON(final String json1, final String json2) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node1 = mapper.readTree(json1);
        final JsonNode node2 = mapper.readTree(json2);

        assertEquals(node1, node2);
    }
}
