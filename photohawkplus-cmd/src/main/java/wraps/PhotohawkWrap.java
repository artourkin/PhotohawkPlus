package wraps;

import at.ac.tuwien.photohawk.commandline.util.ImageReader;
import at.ac.tuwien.photohawk.evaluation.qa.SsimQa;
import dao.ImageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Constants;
import utils.FolderHelper;
import utils.ImageOps;
import utils.PhotoConfigurator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by artur on 28/10/15.
 */
public class PhotohawkWrap {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    SsimQa ssimQa;
    ImageReader ir;
    List<ImageBean> images;
    PhotoConfigurator cfg = PhotoConfigurator.getConfigurator();

    public PhotohawkWrap() {
        logger.info("Initialising SSIM calculator...");
        images= new ArrayList<ImageBean>();
        ir = new ImageReader("dcraw.ssim");
        ssimQa = new SsimQa();
        ssimQa.numThreads(4);
        logger.info("Initialization done.");

    }

    ImageBean calculateSSIM(String file_string1, String file_string2, String file_string1_png, String file_string2_png) throws IOException {
        logger.info("Calculating metrics for " + file_string1 + " and " + file_string2);
        File file1 = new File(file_string1);
        File file2 = new File(file_string2);
        BufferedImage bImage1 = ir.readImage(file1, "dcraw", "dcraw");
        BufferedImage bImage2 = ir.readImage(file2, "dcraw", "dcraw");
        double SSIM = ssimQa.evaluate(bImage1, bImage2).getResult().getChannelValue(0);
        ImageOps.saveImage(bImage1, file_string1_png);
        ImageOps.saveImage(bImage2, file_string2_png);
        ImageBean imageBean = new ImageBean(SSIM, true, file_string1, file_string2, file_string1_png, file_string2_png);
        bImage1.flush();
        bImage2.flush();
        return imageBean;
    }

    public List<ImageBean> getImages() {
        return images;
    }

    public void execute() {
        List<Path> originals = FolderHelper.listFiles(Paths.get(cfg.getProperty(Constants.PATH_PHOTO_ORIGINALS)));
        List<Path> results = FolderHelper.listFiles(Paths.get(cfg.getProperty(Constants.PATH_PHOTO_RESULTS)));
        int i = 0;
        int size = originals.size();
        for (Path original_path : originals) {
            String original_base = FolderHelper.getBaseFilename(original_path.getFileName().toString());
            for (Path result_path : results) {
                String result_base = FolderHelper.getBaseFilename(result_path.getFileName().toString());
                if (!original_base.isEmpty() && original_base.equals(result_base)) {
                    try {
                        i++;
                        cfg.setProperty(Constants.WEB_AJAX_STATUS, "Processing an image " + String.valueOf(i) + " of " + String.valueOf(size - 1) + ".");
                        String original_PNG = (new File(cfg.getProperty(Constants.PATH_TMP_PHOTO) + File.separator + original_path.getFileName().toString() + ".png")).toString();
                        String result_PNG = (new File(cfg.getProperty(Constants.PATH_TMP_PHOTO) + File.separator + result_path.getFileName().toString() + ".png")).toString();
                        ImageBean image = calculateSSIM(original_path.toString(), result_path.toString(), original_PNG, result_PNG);
                        images.add(image);

                    } catch (IOException e) {
                        cfg.setProperty(Constants.WEB_AJAX_STATUS, e.getMessage());
                    }
                }
            }
        }
    }



}
