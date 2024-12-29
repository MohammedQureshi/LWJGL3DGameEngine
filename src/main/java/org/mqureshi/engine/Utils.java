package org.mqureshi.engine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Utils {

    private Utils() {

    }

    public static String readFile(String resourcePath) {
        String content;
        try (InputStream inputStream = ShaderProgram.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + resourcePath);
            }
            content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            throw new RuntimeException("Error reading resource [" + resourcePath + "]", ioException);
        }
        return content;
    }

}
