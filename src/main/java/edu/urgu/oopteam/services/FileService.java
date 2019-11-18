package edu.urgu.oopteam.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class FileService {

    public static FileService getInstance() {
        return LazyHolder.INSTANCE;
    }

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

    private static final class LazyHolder {
        private static final FileService INSTANCE = new FileService();
    }
}
