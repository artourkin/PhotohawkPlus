package cmd;

import junit.framework.TestCase;
import wraps.C3POWrap;
import utils.Constants;
import utils.FolderHelper;
import utils.PhotoConfigurator;

import java.io.File;

/**
 * Created by artur on 22/10/15.
 */
public class photohawkplusCmdTest extends TestCase {

    public void testRunFITS() throws Exception {


        PhotoConfigurator configurator=PhotoConfigurator.getConfigurator();
        configurator.setProperty(Constants.PATH_PHOTO_ORIGINALS,"/Users/artur/Shared/originals");
        configurator.setProperty(Constants.PATH_PHOTO_RESULTS,"/Users/artur/Shared/results");
        configurator.setProperty(Constants.PATH_TMP, FolderHelper.getTempPath());
        configurator.setProperty(Constants.PATH_TMP_PHOTO,FolderHelper.getTempPath() + File.separator + "temp_photohawk_images");
        configurator.setProperty(Constants.PATH_FITS_RESULTS,"/Users/artur/Shared/fits_results");

        photohawkplusCmd cmd=new photohawkplusCmd();
        //cmd.runFITS();


        C3POWrap c3poWrap=new C3POWrap("localhost","27017", "c3po", Constants.PATH_FITS_RESULTS);
       // c3poWrap.uploadFITSmetadata();
        c3poWrap.getSamples();

    }

    public void testRun() throws Exception {

    }


}