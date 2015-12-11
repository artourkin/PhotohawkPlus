package wraps;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Constants;
import utils.FolderHelper;
import utils.PhotoConfigurator;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by artur on 28/10/15.
 */
public class FITSWrapTest extends TestCase {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    PhotoConfigurator configurator=PhotoConfigurator.getConfigurator();
    public void testExecute() throws Exception {
        configurator.setProperty(Constants.PATH_PHOTO_ORIGINALS,"src/test/resources/photos/originals");
        configurator.setProperty(Constants.PATH_PHOTO_RESULTS,"src/test/resources/photos/results");
        configurator.setProperty(Constants.PATH_TMP, FolderHelper.getTempPath());
        configurator.setProperty(Constants.PATH_TMP_PHOTO,FolderHelper.getTempPath() + File.separator + "temp_photohawk_images");
        configurator.setProperty(Constants.PATH_FITS_RESULTS,FolderHelper.getTempPath() + File.separator + "temp_fits_results");
        configurator.setProperty(Constants.PATH_FITS_HOME,"../fits-api/fits-0.8.5");

        FITSWrap fitsWrap=new FITSWrap();
        fitsWrap.execute();

        List<Path> paths = FolderHelper.listFiles(Paths.get(configurator.getProperty(Constants.PATH_FITS_RESULTS)));
        logger.info("Created fits files:");
        for (Path p: paths){
            logger.info(p.toString());
        }

    }
}