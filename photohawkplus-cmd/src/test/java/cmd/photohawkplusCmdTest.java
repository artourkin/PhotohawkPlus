package cmd;

import dao.ImageBean;
import junit.framework.TestCase;
import org.apache.cxf.io.CachedOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wraps.C3POWrap;
import utils.Constants;
import utils.FolderHelper;
import utils.PhotoConfigurator;

import java.io.File;
import java.util.List;

/**
 * Created by artur on 22/10/15.
 */
public class photohawkplusCmdTest extends TestCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    PhotoConfigurator configurator=PhotoConfigurator.getConfigurator();
    public void setUp(){

        configurator.setProperty(Constants.PATH_PHOTO_ORIGINALS,"src/test/resources/photos/originals");
        configurator.setProperty(Constants.PATH_PHOTO_RESULTS,"src/test/resources/photos/results");
        configurator.setProperty(Constants.PATH_TMP, FolderHelper.getTempPath());
        configurator.setProperty(Constants.PATH_TMP_PHOTO,FolderHelper.getTempPath() + File.separator + "temp_photohawk_images");
        configurator.setProperty(Constants.PATH_FITS_RESULTS,FolderHelper.getTempPath() + File.separator + "temp_fits_results");
        configurator.setProperty(Constants.PATH_FITS_HOME,"../fits-api/fits-0.8.5");
        configurator.setProperty(Constants.WEB_AJAX_STATUS, ".");
    }


    public void testRun() throws Exception {
        photohawkplusCmd cmd = new photohawkplusCmd();
        if (!cmd.isBusy()) {
            cmd.run();
        }
        while (cmd.isBusy()){
            System.out.println(configurator.getProperty(Constants.WEB_AJAX_STATUS));
            Thread.sleep(1000);
        }




    }


}