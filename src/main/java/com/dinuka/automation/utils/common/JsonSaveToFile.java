package com.dinuka.automation.utils.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class JsonSaveToFile {

    private static final Logger logger = LoggerFactory.getLogger(JsonSaveToFile.class);

    private JsonSaveToFile() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void saveJsonToFile(Object jsonData, String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        File file = new File(filePath);

        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try {
            objectMapper.writeValue(file, jsonData);
            logger.info("JSON saved to: {}", filePath);
        } catch (IOException e) {
            logger.error("Error saving JSON to file: {}", e.getMessage());
        }
    }
}