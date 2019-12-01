package edu.urgu.oopteam.services;

import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
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
}
