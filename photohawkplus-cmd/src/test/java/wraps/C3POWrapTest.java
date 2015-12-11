package wraps;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Constants;
import utils.FolderHelper;
import utils.PhotoConfigurator;

import java.io.File;
import java.util.List;

/**
 * Created by artur on 28/10/15.
 */
public class C3POWrapTest extends TestCase {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    PhotoConfigurator configurator=PhotoConfigurator.getConfigurator();
    public void testExecute() throws Exception {
        C3POWrap c3poWrap=new C3POWrap("localhost","27017", "c3po",configurator.getProperty( Constants.PATH_FITS_RESULTS));
        c3poWrap.execute();
        List<String> samples = c3poWrap.getSamples();
        for(String s: samples){
            System.out.println(s);
        }
    }

    public void setUp(){

        configurator.setProperty(Constants.PATH_PHOTO_ORIGINALS,"src/test/resources/photos/originals");
        configurator.setProperty(Constants.PATH_PHOTO_RESULTS,"src/test/resources/photos/results");
        configurator.setProperty(Constants.PATH_TMP, FolderHelper.getTempPath());
        configurator.setProperty(Constants.PATH_TMP_PHOTO,FolderHelper.getTempPath() + File.separator + "temp_photohawk_images");
        configurator.setProperty(Constants.PATH_FITS_RESULTS,FolderHelper.getTempPath() + File.separator + "temp_fits_results");
        configurator.setProperty(Constants.PATH_FITS_HOME,"../fits-api/fits-0.8.5");
        FITSWrap fitsWrap=new FITSWrap();
        fitsWrap.execute();

    }

    public void tearDown(){

        
    }




}