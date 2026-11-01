package com.dinuka.automation.utils.api.restassured;

import java.io.FileWriter;
import java.io.IOException;

import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseHelpers {

      private static final Logger logger = LoggerFactory.getLogger(ResponseHelpers.class);

    private ResponseHelpers() {
        throw new UnsupportedOperationException("Utility class");
    }  

    // Prints the response body to the console in a pretty format
    public static void printResponseBody(Response response) {
        logger.info("Response Body: ");
        logger.info(response.getBody().asPrettyString());
    }

    // Prints the response headers to the console
    public static void printResponseHeaders(Response response) {
        logger.info("Response Headers:");
        response.getHeaders().forEach(header -> logger.info("{}", header));
    }

    // Prints the full response: Status Code, Headers, and Body
    public static void printFullResponse(Response response) {
        logger.info("Status Code: " + response.getStatusCode());
        logger.info("Headers:");
        response.getHeaders().forEach(header -> logger.info("{}", header));
        logger.info("Body:");
        logger.info(response.getBody().asPrettyString());
    }

 public static void saveResponseToFile(Response response, String filePath) {
    try (FileWriter writer = new FileWriter(filePath)) {
        writer.write(response.getBody().asPrettyString());
    } catch (IOException e) {
        logger.error("Failed to save response to file: {}", filePath, e);
    }
}

      public static Object getJsonValue(Response response, String jsonPath) {
        return response.jsonPath().get(jsonPath);
    }

}
