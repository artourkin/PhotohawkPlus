package utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by artur on 21/09/15.
 */
public class FolderHelper {

    public static String getAssetsPath(Boolean isProd) {
        if (!isProd) {
            return "src/main/java/assets";
        }
        return "/assets";
    }

    public static String getTempPath() {
        return System.getProperty("java.io.tmpdir");
    }

    public static String getBaseFilename(String filepath) {
        return filepath.split("\\.(?=[^\\.]+$)")[0];
    }

    public static List<Path> listFiles(Path path) {
        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    result.addAll(listFiles(entry));
                }
                if (!Files.isDirectory(entry)) {
                    result.add(entry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
