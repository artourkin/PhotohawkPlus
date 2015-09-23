package utils;


import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by artur on 20/09/15.
 */
public class ImageOps {
    private static final Logger logger = LoggerFactory.getLogger(ImageOps.class);
    public static String downscale(String original, String target) throws IOException {
        logger.debug("Downscaling " +original +" to " +target);
        File fileOriginal = new File(original);
        File fileTarget = new File(target);
        BufferedImage tmp = ImageIO.read(fileOriginal);
        tmp = Scalr.resize(tmp, Scalr.Method.AUTOMATIC, 500);
        ImageIO.write(tmp, "PNG", fileTarget);
        tmp.flush();
        return target;
    }

}
