package utils;

import dao.ImageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by artur on 16/09/15.
 */
public class CSVWriter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    File file;
    String[] header;
    ICsvBeanWriter beanWriter = null;
    CellProcessor[] processors = getProcessors();
    private static CellProcessor[] getProcessors() {

        final CellProcessor[] processors = new CellProcessor[] {
                new ParseDouble(), // SSIM
                null,//new Optional(new FmtBool("Y", "N")),// new FmtBool("true", "false"), // isSimilar
                new NotNull(), // Original
                new NotNull(), // Result
                new NotNull(), // Original_PNG
                new NotNull() // Result_PNG

        };

        return processors;
    }



    public CSVWriter(File file) throws IOException {
        this.file=file;
        header= new String[] { "SSIM", "isSimilar" ,"original", "result", "originalPNG", "resultPNG" };

            beanWriter = new CsvBeanWriter(new FileWriter(file.getAbsolutePath()),
                    CsvPreference.STANDARD_PREFERENCE);
            beanWriter.writeHeader(header);

    }


    public void write(ImageBean image) throws IOException {
        logger.debug("Writing an image to CSV:" + image.toString());
        beanWriter.write(image, header, processors);
    }

    public void destroy() throws IOException {

            beanWriter.close();

    }




}
