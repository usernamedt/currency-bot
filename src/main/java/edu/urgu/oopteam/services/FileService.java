package edu.urgu.oopteam.services;

import java.io.*;
import java.net.URL;

public class FileService {

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

    /**
     * Save file to resource folder
     * @param fileName filename
     * @param content file content
     * @param fileExtension file extension
     */
    private static void saveResourceFile(String fileName, String content, String fileExtension) throws IOException {
        var saveDirectory = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources";
        var file = new File(saveDirectory, fileName + fileExtension);
        if (!file.createNewFile())
            throw new IOException("Ошибка при создании файла");

        try(var writer = new FileWriter(file.getAbsolutePath(), false))
        {
            writer.write(content);
            writer.flush();
        }
        catch(IOException exception){
            throw new IOException("Ошибка при записи в файл");
//            System.out.println(exception.getMessage());
//            System.exit(-1);
        }
    }
}
