package utils;

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
    private static CellProcessor[] getProcessors() {

        final CellProcessor[] processors = new CellProcessor[] {
                new ParseDouble(), // SSIM
                new ParseBool(), // isSimilar
                new NotNull(), // Original
                new NotNull(), // Result
                new NotNull(), // Original_PNG
                new NotNull() // Result_PNG

        };

        return processors;
    }


    public static List<ImageBean> readWithCsvBeanReader(String path) throws Exception {
        ICsvBeanReader beanReader = null;
        File file=new File(path);
        List<ImageBean> images=new ArrayList<>();
        try {
            beanReader = new CsvBeanReader(new FileReader(file), CsvPreference.STANDARD_PREFERENCE);

            // the header elements are used to map the values to the bean (names must match)
            final String[] header = beanReader.getHeader(true);
            final CellProcessor[] processors = getProcessors();

            ImageBean image;
            while( (image = beanReader.read(ImageBean.class, header, processors)) != null ) {
                images.add(image);
            }

        }
        finally {
            if( beanReader != null ) {
                beanReader.close();
            }
        }
        return images;

    }


}
