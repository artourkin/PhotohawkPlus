package nl.knaw.dans.fits;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.jdom.Document;
import org.jdom.Namespace;
import org.junit.BeforeClass;
import org.junit.Test;

public class FitsWrapTest
{
    
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        InputStream ins = FitsWrap.class.getClassLoader().getResourceAsStream("fits.properties");
        if (ins != null) {
            Properties props = new Properties();
            props.load(ins);
            // somehow ${project.basedir} resolves to another directory when build from the command line
            String base = new File(props.getProperty("project.build.sourceDirectory")).getParentFile().getParentFile().getParent();
            String fitsHome = base + File.separator + props.getProperty("fits.version");
            FitsWrap.setFitsHome(fitsHome);
        } else {
            throw new IllegalStateException("The file 'fits.properties' was not found on the classpath.");
        }   
    }
    
    
    
    @Test
    public void testInstance() throws Exception {
        FitsWrap fits1 = FitsWrap.instance();
        FitsWrap fits2 = FitsWrap.instance();
        assertEquals(fits1, fits2);
        
    }
    
    @Test
    public void testGetDocument() throws Exception {
    //    FitsWrap fits = FitsWrap.instance();
    //    Document doc = fits.extract(new File(this.getClass().getClassLoader().getResource("test-files/DSC00323.jpg").toString()));
        //System.err.println(new XMLOutputter(Format.getPrettyFormat()).outputString(doc)); 
    //    Namespace ns = Namespace.getNamespace("http://hul.harvard.edu/ois/xml/ns/fits/fits_output");
    //    assertTrue(doc.getRootElement().getChild("identification", ns).getChildren().size() == 1);
    //    assertTrue(doc.getRootElement().getChild("fileinfo", ns).getChildren().size() > 0);
    //    assertTrue(doc.getRootElement().getChild("filestatus", ns).getChildren().size() > 0);
    //    assertTrue(doc.getRootElement().getChild("metadata", ns).getChildren().size() > 0);
    }
    
    @Test
    public void testGetToolInfo() throws Exception {
        FitsWrap fits = FitsWrap.instance();
        List<String> infoList = fits.getToolInfo();
//        for (String info : infoList) {
//            System.err.println(info);
//        }
        assertTrue(infoList.size() > 0);
    }
    

}
