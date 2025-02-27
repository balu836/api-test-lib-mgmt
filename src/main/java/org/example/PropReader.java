package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class PropReader {
    static PropReader propReader;
    static Properties properties;

    PropReader() throws IOException {
        properties = new Properties();
        properties.load(Files.newInputStream(Paths.get(ClassLoader.getSystemResource("app.properties").getFile())));
    }

    public static Properties get() throws IOException {
        if (properties == null) {
            propReader = new PropReader();
        }
        return properties;
    }
}
