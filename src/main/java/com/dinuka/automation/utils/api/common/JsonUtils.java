package com.dinuka.automation.utils.api.common;

import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Converts a given Map of key-value pairs into a JSON-formatted string.
 *
 * This method is useful when dynamically creating JSON payloads at runtime,
 * such as for API requests or logging purposes. It leverages Jackson's
 * ObjectMapper to serialize the provided data map into a proper JSON string.
 * If any serialization error occurs, it throws a JsonProcessingException.
 *
 * Example input: {"username"="john", "age"=30}
 * Output: {"username":"john","age":30}
 */

public class JsonUtils {

    private JsonUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String createJsonFromMap(Map<String, Object> dataMap) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(dataMap);
    }
}