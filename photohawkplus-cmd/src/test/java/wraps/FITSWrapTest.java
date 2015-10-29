package wraps;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Constants;
import utils.FolderHelper;
import utils.PhotoConfigurator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by artur on 28/10/15.
 */
public class FITSWrapTest extends TestCase {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public void testExecute() throws Exception {
        PhotoConfigurator.getConfigurator().setProperty(Constants.PATH_FITS_RESULTS, "/Users/artur/Shared/fits_results");
        PhotoConfigurator.getConfigurator().setProperty(Constants.PATH_PHOTO_ORIGINALS, "/Users/artur/Shared/originals");
        FITSWrap fitsWrap=new FITSWrap();
        fitsWrap.execute();

        List<Path> paths = FolderHelper.listFiles(Paths.get(PhotoConfigurator.getConfigurator().getProperty(Constants.PATH_FITS_RESULTS)));
        for (Path p: paths){
            logger.info(p.toString());
        }

    }
}