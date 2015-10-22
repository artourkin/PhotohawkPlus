package nl.knaw.dans.fits;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.jdom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsConfigurationException;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.tools.Tool;

/*
 * A simple singleton wrapper class around an instance of {@link Fits}. <br/>
 * Fits looks for resources in the FITS_HOME directory, which is the directory containing the XML and
 * tools directories of a FITS distribution. You can specify where the FITS_HOME directory is either by
 * <ul>
 * <li>calling {@link FitsWrap#setFitsHome(String)} prior to calling {@link FitsWrap#instance()};</li>
 * <li>setting a System.Property with the name 'FITS_HOME';</li>
 * <li>setting an environment property with the name 'FITS_HOME'.</li>
 * </ul>
 * The value of the FITS_HOME variable will be determined in the order given above. <br/>
 * Fits can be configured in {FITS_HOME}/xml/fits.xml. See also: <a
 * href="http://projects.iq.harvard.edu/fits/fits-configuration-files" >fits-configuration-files</a>.
 */
public class FitsWrap
{

    /**
     * The property key to denote the FITS_HOME variable.
     */
    public static final String PROPERTY_KEY_FITS_HOME = "FITS_HOME";

    private static final Logger logger = LoggerFactory.getLogger(FitsWrap.class);

    private static String FITS_HOME = null;
    private static FitsWrap INSTANCE = null;
    private static Fits FITS = null;

    private FitsWrap() throws FitsConfigurationException
    {
        findHome();
        BasicConfigurator.configure();
        FITS = new Fits(FITS_HOME);
    }

    private void findHome()
    {
        if (FITS_HOME != null && !FITS_HOME.equals(""))
        {
            logger.info("FITS_HOME was set: {}", FITS_HOME);
        }
        else if ((FITS_HOME = System.getProperty(PROPERTY_KEY_FITS_HOME)) != null)
        {
            logger.info("FITS_HOME was read from System.properties: {}", FITS_HOME);
        }
        else if ((FITS_HOME = System.getenv(PROPERTY_KEY_FITS_HOME)) != null)
        {
            logger.info("FITS_HOME was read from environment: {}", FITS_HOME);
        }
        else
        {
            String msg = PROPERTY_KEY_FITS_HOME + " was not set." //
                    + "\nEither specify the System.property " + PROPERTY_KEY_FITS_HOME //
                    + "\nor specify the environment variable " + PROPERTY_KEY_FITS_HOME //
                    + "\nor call setFitsHome(String) before instantiating this class.";
            throw new IllegalStateException(msg);
        }

    }

    /**
     * Set the FITS_HOME directory. Fits looks for resources in the FITS_HOME directory, which is the
     * directory containing the XML and tools directories.
     * 
     * @param fitsHome
     *        the directory containing the XML and tools directories
     * @throws IllegalStateException
     *         if set with a different value after instantiating this class.
     */
    public static void setFitsHome(String fitsHome)
    {
        if (INSTANCE != null && !FITS_HOME.equals(fitsHome))
        {
            String msg = FitsWrap.class.getSimpleName() + " already initialized with FITS_HOME " + FITS_HOME;
            throw new IllegalStateException(msg);
        }
        FITS_HOME = fitsHome;
    }

    /**
     * Get the FITS_HOME directory. May be null.
     * 
     * @return the FITS_HOME directory.
     */
    public static String getFitsHome()
    {
        return FITS_HOME;
    }

    /**
     * Obtain an instance of {@link FitsWrap}.
     * 
     * @return an instance of {@link FitsWrap}
     * @throws FitsConfigurationException
     *         when something is wrong with the Fits configuration.
     * @throws IllegalStateException
     *         if the value of the fits home directory could not be determined.
     */
    public static FitsWrap instance() throws FitsConfigurationException
    {
        if (INSTANCE == null)
        {
            INSTANCE = new FitsWrap();
        }
        return INSTANCE;
    }

    /**
     * Process a file.
     * 
     * @param file
     *        the file to be processed.
     * @return an instance of FitsOutput.
     * @throws FitsException
     *         in case of failure.
     */
    public FitsOutput process(File file) throws FitsException
    {
        return FITS.examine(file);
    }

    /**
     * Convenience method to obtain the fits-xml output.
     * 
     * @param file
     *        the file to be processed.
     * @return an instance of FitsOutput.
     * @throws FitsException
     *         in case of failure.
     */
    public Document extract(File file) throws FitsException
    {
        return process(file).getFitsXml();
    }

    public List<String> getToolInfo() throws Exception
    {
        List<String> infoList = new ArrayList<String>();
        for (Tool tool : FITS.getToolbelt().getTools())
        {
            infoList.add(tool.getToolInfo().print());
        }
        return infoList;
    }

    public boolean isStatisticsEnabled()
    {
        return Fits.enableStatistics;
    }

    public void setStatisticsEnabled(boolean enabled)
    {
        Fits.enableStatistics = enabled;
    }
}
