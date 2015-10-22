package cmd;

import junit.framework.TestCase;
import utils.FolderHelper;

import java.io.File;

/**
 * Created by artur on 22/10/15.
 */
public class photohawkplusCmdTest extends TestCase {

    public void testRunFITS() throws Exception {

        photohawkplusCmd cmd=new photohawkplusCmd("/Users/artur/Shared/originals", "/Users/artur/Shared/results", FolderHelper.getTempPath(),FolderHelper.getTempPath() + File.separator + "temp_photohawk_images");
        cmd.runFITS("/Users/artur/Shared/originals","/Users/artur/Shared/fits_results");

    }
}