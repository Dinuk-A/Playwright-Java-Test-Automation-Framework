package com.dinuka.automation.utils.api.restassured;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.restassured.response.Response;
import java.util.function.Consumer;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class StreamReaderUtils {

    private static final Logger logger = LoggerFactory.getLogger(StreamReaderUtils.class);

    private StreamReaderUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static List<String> readStreamForDuration(Response response, long durationMillis,
            Consumer<String> lineConsumer) throws InterruptedException {
        List<String> lines = new ArrayList<>();

        runStreamThread(response, durationMillis, reader -> {
            try {
                String line;
                long start = System.currentTimeMillis();
                while ((System.currentTimeMillis() - start) < durationMillis &&
                        (line = reader.readLine()) != null) {
                    if (lineConsumer != null) {
                        lineConsumer.accept(line);
                    }
                    lines.add(line);
                }
            } catch (IOException e) {
                logger.error("Error reading stream: {}", e.getMessage(), e);
            }
        });

        return lines;
    }

    public static List<String> readStreamForDuration(Response response, long durationMillis)
            throws InterruptedException {
        return readStreamForDuration(response, durationMillis,
                line -> logger.info("Stream: {}", line));
    }

    public static String readFullStreamAsString(Response response, long durationMillis)
            throws InterruptedException {
        StringBuilder fullStream = new StringBuilder();

        runStreamThread(response, durationMillis, reader -> {
            try {
                String line;
                long start = System.currentTimeMillis();
                while ((System.currentTimeMillis() - start) < durationMillis &&
                        (line = reader.readLine()) != null) {
                    fullStream.append(line).append("\n");
                }
            } catch (IOException e) {
                logger.error("Error reading full stream: {}", e.getMessage(), e);
            }
        });

        return fullStream.toString();
    }

    /**
     * Shared boilerplate: wraps a stream-reading task in a thread with a CountDownLatch timeout.
     */
    private static void runStreamThread(Response response, long durationMillis,
            Consumer<BufferedReader> task) throws InterruptedException {
        InputStream stream = response.asInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        CountDownLatch latch = new CountDownLatch(1);

        Thread streamReaderThread = new Thread(() -> {
            try {
                task.accept(reader);
            } finally {
                closeResources(reader, stream);
                latch.countDown();
            }
        });

        streamReaderThread.start();

        boolean completed = latch.await(durationMillis + 2000, TimeUnit.MILLISECONDS);
        if (!completed) {
            streamReaderThread.interrupt();
            throw new IllegalStateException("Timed out waiting for stream reader thread");
        }
    }

    private static void closeResources(BufferedReader reader, InputStream stream) {
        try {
            if (reader != null) {
                reader.close();
            }
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            logger.error("Error closing stream resources: {}", e.getMessage(), e);
        }
    }
}