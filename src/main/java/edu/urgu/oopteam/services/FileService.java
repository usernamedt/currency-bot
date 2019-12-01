package edu.urgu.oopteam.services;

import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;

@Service
public class FileService {
    /**
     * Read file from resource folder
     *
     * @param fileName filename
     * @return file as a FileInputStream
     */
    public FileInputStream readResourceFile(String fileName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new FileInputStream(resource.getFile());
        }
    }

    /**
     * Read file from resource folder
     *
     * @param fileName filename
     * @return file as a FileInputStream
     */
    public String readResourceFileAsString(String fileName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            var tokenFile = new File(resource.getFile());
            var fr = new FileReader(tokenFile);
            var reader = new BufferedReader(fr);
            String line;
            var builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            return builder.toString();
        }
    }
}
