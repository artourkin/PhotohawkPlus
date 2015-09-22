package utils;

import junit.framework.TestCase;
import dao.*;

import java.util.List;

public class CSVReaderTest extends TestCase {

    public void testReadWithCsvBeanReader() throws Exception {

        List<ImageBean> imageBeans = CSVReader.readWithCsvBeanReader("src/test/java/resources/images.csv");
        assertEquals(imageBeans.size(),2);
    }
}