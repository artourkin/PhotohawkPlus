package wraps;

import nl.knaw.dans.fits.FitsWrap;
import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Constants;
import utils.FolderHelper;
import utils.PhotoConfigurator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by artur on 28/10/15.
 */
public class FITSWrap {
    FitsWrap fitsWrap;

    PhotoConfigurator cfg = PhotoConfigurator.getConfigurator();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public FITSWrap(){
        FitsWrap.setFitsHome("../fits-api/fits-0.8.5");
        try {
            fitsWrap = FitsWrap.instance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void runFITS(String Originals, String fits_results) {
        cfg.setProperty(Constants.WEB_AJAX_STATUS, "Characterising photographs using FITS.");
        List<Path> originals = FolderHelper.listFiles((new File(Originals)).toPath());
        int i = 0;
        int size = originals.size();
        for (Path original_path : originals) {
            try {
                i++;
                PhotoConfigurator.getConfigurator().setProperty(Constants.WEB_AJAX_STATUS, "Characterising an image " + String.valueOf(i) + " of " + String.valueOf(size - 1) + ".");
                Document document = fitsWrap.extract(original_path.toFile());
                XMLOutputter xmlOutput = new XMLOutputter();
                xmlOutput.setFormat(Format.getPrettyFormat());
                xmlOutput.output(document, new FileWriter(fits_results + File.separator + original_path.getFileName().toString() + ".xml"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void cleanOutputFolder(){

        try {
            FileUtils.cleanDirectory(new File(cfg.getProperty(Constants.PATH_FITS_RESULTS)));
        } catch (IOException e) {
            logger.info("Could not clean the FITS results folder");
        }

    }

    public void execute() {
        cleanOutputFolder();
        runFITS(cfg.getProperty(Constants.PATH_PHOTO_ORIGINALS), cfg.getProperty(Constants.PATH_FITS_RESULTS));
    }


}
