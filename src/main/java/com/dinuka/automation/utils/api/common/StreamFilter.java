package com.dinuka.automation.utils.api.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamFilter {

    private static final Logger logger = LoggerFactory.getLogger(StreamFilter.class);

    private StreamFilter() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static JsonNode extractFieldFromStream(String streamResponse, String field, String value) {
        ObjectMapper mapper = new ObjectMapper();
        String[] lines = streamResponse.split("\n");

        for (String line : lines) {
            if (line.startsWith("data:")) {
                String json = line.substring(5).trim();
                try {
                    JsonNode node = mapper.readTree(json);
                    if (value.equals(node.get(field).asText())) {
                        return node;
                    }
                } catch (Exception e) {
                    logger.info("Invalid JSON in stream: {}", json);
                }
            }
        }

        return null;
    }
}