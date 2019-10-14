package edu.urgu.oopteam.services;

import java.io.*;
import java.net.URL;

public class FileService {

    private static final class LazyHolder
    {
        private static final FileService INSTANCE = new FileService();
    }

    public static FileService getInstance()
    {
        return LazyHolder.INSTANCE;
    }

    /**
     * Read file from resource folder
     * @param fileName filename
     * @return file as a string
     */
    public String readResourceFile(String fileName) throws IOException {
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

    public static void saveFile(String fileName, String saveDirectory, String content, String fileExtension) throws IOException {
        var file = new File(saveDirectory, fileName + fileExtension);
        if (!file.createNewFile())
            throw new IOException("Ошибка при создании файла");

        try(var writer = new FileWriter(file.getAbsolutePath(), false))
        {
            writer.write(content);
            writer.flush();
        }
        catch(IOException ex){
            throw new IOException("Ошибка при записи в файл");
//            System.out.println(ex.getMessage());
        }
    }
}
