package edu.urgu.oopteam.services;

import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Service
public class FileService {
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
            // Эта матрёшка нужна, чтобы при сборке в Maven, когда считывается файл в тесте,
            // явно указывалась кодировка, иначе возникает AssertionError.
            var reader = new BufferedReader(new InputStreamReader(new FileInputStream(tokenFile), StandardCharsets.UTF_8));
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
