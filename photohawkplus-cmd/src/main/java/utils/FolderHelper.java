package utils;

/**
 * Created by artur on 21/09/15.
 */
public class FolderHelper {

    public static String getAssetsPath(Boolean isProd){
        if (!isProd){
            return "src/main/java/assets";
        }
        return "/assets";
    }
    public static String getTempPath(){
        return System.getProperty("java.io.tmpdir");
    }

    public static String getBaseFilename(String filepath){
        return filepath.split("\\.(?=[^\\.]+$)")[0];
    }
}
