package org.graylog.plugins.jira.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public final class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.registerModule(new Jdk8Module());
    }

    private JsonUtil() {}

    public static <T> T convertValue(Object val, TypeReference<T> typeRef) {
        return MAPPER.convertValue(val, typeRef);
    }

    public static <T> T readValue(String json, Class<T> klass) throws JsonProcessingException {
        return MAPPER.readValue(json, klass);
    }

    public static String writeValueAsString(Object val) throws JsonProcessingException {
        return MAPPER.writeValueAsString(val);
    }
}
