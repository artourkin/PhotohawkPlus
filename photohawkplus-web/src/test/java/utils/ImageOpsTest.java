package utils;

import junit.framework.TestCase;

public class ImageOpsTest extends TestCase {

    public void testDownscale() throws Exception {
        String result=ImageOps.downscale("src/test/java/resources/temp_images/RAW_NIKON_D1X.dng.png", "src/test/java/resources/temp_images_resized/RAW_NIKON_D1X.dng.png");
        ImageOps.downscale("src/test/java/resources/temp_images/RAW_NIKON_D1.dng.png", "src/test/java/resources/temp_images_resized/RAW_NIKON_D1.dng.png");
        ImageOps.downscale("src/test/java/resources/temp_images/RAW_NIKON_D1X.NEF.png", "src/test/java/resources/temp_images_resized/RAW_NIKON_D1X.NEF.png");
        ImageOps.downscale("src/test/java/resources/temp_images/RAW_NIKON_D1.NEF.png", "src/test/java/resources/temp_images_resized/RAW_NIKON_D1.NEF.png");
        assertNotSame(result, "");
    }
}