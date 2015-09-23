package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import dao.*;

/**
 * Created by artur on 20/09/15.
 */
public class CSVReader {
    private static final Logger logger = LoggerFactory.getLogger(CSVReader.class);
    private static CellProcessor[] getReadingProcessors() {

        return new CellProcessor[] {
                new ParseDouble(), // SSIM
                new ParseBool(), // isSimilar
                new NotNull(), // Original
                new NotNull(), // Result
                new NotNull(), // Original_PNG
                new NotNull() // Result_PNG

        };
    }


    public static List<ImageBean> readWithCsvBeanReader(String path) throws Exception {
        logger.debug("Reading a CSV from " + path);
        File file=new File(path);
        List<ImageBean> images=new ArrayList<>();
        ICsvBeanReader beanReader = new CsvBeanReader(new FileReader(file), CsvPreference.STANDARD_PREFERENCE);

        final String[] header = beanReader.getHeader(true);
        final CellProcessor[] processors = getReadingProcessors();

        ImageBean image;
        while( (image = beanReader.read(ImageBean.class, header, processors)) != null ) {
            images.add(image);
        }
        if( beanReader != null ) {
            beanReader.close();
        }

        return images;

    }


}
