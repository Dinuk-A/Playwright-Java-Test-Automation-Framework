package com.dinuka.automation.utils.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.io.File;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonReader {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonReader() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String readStaticJsonFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read JSON file: " + filePath, e);
        }
    }

    public static String loadAndReplaceJsonPlaceholders(String filePath, Map<String, String> placeholders) {
        try {
            String json = new String(Files.readAllBytes(Paths.get(filePath)));

            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                json = json.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            return json;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read JSON file: " + filePath, e);
        }
    }

    public static Object fetchJsonValueByKey(String filePath, String keyString) {
        try {
            JsonNode rootNode = objectMapper.readTree(new File(filePath));
            JsonNode valueNode = rootNode.get(keyString);
            return valueNode != null ? objectMapper.treeToValue(valueNode, Object.class) : null;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read JSON file: " + filePath, e);
        }
    }

    public static <T> T readJsonFileOnce(String filePath, Class<T> valueType) {
        File jsonFile = new File(filePath);
        if (!jsonFile.exists()) {
            throw new IllegalStateException("JSON file not found: " + filePath);
        }
        try {
            return objectMapper.readValue(jsonFile, valueType);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read JSON from file: " + filePath, e);
        }
    }

    public static String fetchJsonValueByPath(String filePath, String jsonPath) {
        try {
            JsonNode rootNode = objectMapper.readTree(new File(filePath));

            String[] pathParts = jsonPath.split("\\.");
            JsonNode currentNode = rootNode;

            for (String pathPart : pathParts) {
                currentNode = currentNode.get(pathPart);

                if (currentNode == null) {
                    return null;
                }
            }

            return currentNode.isValueNode()
                    ? currentNode.asText()
                    : currentNode.toString();

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to read JSON file: " + filePath, e);
        }
    }
}