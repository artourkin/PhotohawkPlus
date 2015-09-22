package utils;

import ninja.AssetsController;
import ninja.AssetsControllerHelper;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by artur on 20/09/15.
 */
public class ImageOps {

    public static String downscale(String original) {
        String result="";
        File file = new File(original);
        String filename=getBaseFilename(file.getName());
        String tempDir = System.getProperty("java.io.tmpdir");//"src/main/java/assets/images";//System.AssetsController.ASSETS_DIR;//System.getProperty("java.io.tmpdir");
        result= tempDir+filename+"_resized.png";
        downscale(original,result);
        return result;
    }

    public static String downscale(String original, String target) {

        BufferedImage tmp = null;
        try {
            File file = new File(original);
            tmp = ImageIO.read(file);
            tmp = Scalr.resize(tmp, Scalr.Method.AUTOMATIC, 500);
            ImageIO.write(tmp, "PNG", new File(target));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert tmp != null;
            tmp.flush();
        }
        return target;
    }

    static String getBaseFilename(String filepath){
        return filepath.split("\\.(?=[^\\.]+$)")[0];
    }

}
