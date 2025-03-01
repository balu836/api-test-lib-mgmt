package org.example;

import java.io.*;

public class FileUtils {
    /**
     *
     * @param fileNameWithLocation File name with location
     * @return returned file content as a String
     */
    public static String readFile(String fileNameWithLocation) {
        String content = null;
        File file = new File(fileNameWithLocation);
        try (FileReader reader = new FileReader(file)) {
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
}
