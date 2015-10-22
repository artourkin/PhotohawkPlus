package cmd;

import at.ac.tuwien.photohawk.commandline.util.ImageReader;
import at.ac.tuwien.photohawk.evaluation.qa.SsimQa;
import dao.ImageBean;
import nl.knaw.dans.fits.FitsWrap;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CSVWriter;
import utils.PhotoConfigurator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class photohawkplusCmd {
    SsimQa ssimQa;
    ImageReader ir;
    File folderOriginals, folderResults, folderTmp, folderFits;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    File folderTmpImages;
    CSVWriter csvWriter;
    List<ImageBean> imageBeans;
    public static void main(String[] args) {
        if (args.length !=4) {
            System.out.println("Please specify the following:/path/to/originals /path/to/results /path/to/tmp_results /path/to/tmp_image_results");
            return;
        }
        //photohawkplusCmd m=new photohawkplusCmd(args[0],args[1],args[2], args[3]);

       // if (!m.folderOriginals.isDirectory() || !m.folderResults.isDirectory())
       //     return;
       // m.init();
       // m.run();
    }
    public photohawkplusCmd(String path_to_originals, String path_to_results, String path_to_tmp, String path_to_tmp_images, String path_to_fits_results){
        PhotoConfigurator configurator = PhotoConfigurator.getConfigurator();
        configurator.setProperty("folderOriginals", path_to_originals);


        folderOriginals =new File(path_to_originals);
        folderResults =new File(path_to_results);
        folderTmp =new File(path_to_tmp);
        folderFits=new File(path_to_fits_results);
        File dirToCreate=new File(path_to_tmp_images);
        try {
            folderTmpImages = Files.createDirectories(dirToCreate.toPath()).toFile();
            csvWriter=new CSVWriter(new File(folderTmp.toString()+File.separator+"images.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug("Initialising SSIM calculator...");
        ir = new ImageReader("dcraw.ssim");
        imageBeans=new ArrayList<>();
        ssimQa = new SsimQa();
        ssimQa.numThreads(4);
        logger.debug("Initialization done.");
    }

    private void init() {


    }
    public ImageBean calculateSSIM(String file_string1, String file_string2, String file_string1_png, String file_string2_png) throws IOException {
        logger.debug("Calculating metrics for " + file_string1 + " and " + file_string2);
        File file1 = new File(file_string1);
        File file2 = new File(file_string2);
        BufferedImage bImage1 = getBufferedImage(file1);
        BufferedImage bImage2 = getBufferedImage(file2);
        double SSIM=ssimQa.evaluate(bImage1, bImage2).getResult().getChannelValue(0);
        saveImage(bImage1,file_string1_png);
        saveImage(bImage2,file_string2_png);
        ImageBean imageBean=new ImageBean(SSIM, true,file_string1,file_string2,file_string1_png, file_string2_png);
        bImage1.flush();
        bImage2.flush();

        return imageBean;
    }

    public List<ImageBean> run() {
        List<ImageBean> result=new ArrayList<>();
        List<Path> originals=listFiles(folderOriginals.toPath());
        List<Path> results=listFiles(folderResults.toPath());
        for (Path original_path: originals){
            String original_base=getBaseFilename(original_path.getFileName().toString());
            for (Path result_path: results){
                String result_base=getBaseFilename(result_path.getFileName().toString());
                if (!original_base.isEmpty() && original_base.equals(result_base)){
                    try {
                        logger.debug("Calculating metrics for " + original_path.getFileName());
                        BufferedImage bImage1 = getBufferedImage(original_path.toFile());
                        BufferedImage bImage2 = getBufferedImage(result_path.toFile());
                        double SSIM=ssimQa.evaluate(bImage1, bImage2).getResult().getChannelValue(0);
                        String original_PNG=saveImage(original_path, bImage1);
                        String result_PNG=saveImage(result_path, bImage2);
                        bImage1.flush();
                        bImage2.flush();
                        ImageBean imageBean=new ImageBean(SSIM, true,original_path.toString(),result_path.toString(),original_PNG, result_PNG);
                        result.add(imageBean);
                        csvWriter.write(imageBean);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
        try {
            csvWriter.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
    private String saveImage(Path original, BufferedImage bImage1) throws IOException {
        File file=new File(folderTmpImages.toString() + File.separator + original.getFileName().toString()+".png");
        ImageIO.write(bImage1, "png", file);
        return file.getAbsolutePath();
    }

    private String saveImage(BufferedImage bImage1, String pathToSave) throws IOException {
        File file=new File(pathToSave);
        ImageIO.write(bImage1, "png", file);
        return file.getAbsolutePath();
    }

    public String getBaseFilename(String filepath){
        return filepath.split("\\.(?=[^\\.]+$)")[0];
    }

    public List<Path> listFiles(Path path) {
        List<Path> result=new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    result.addAll(listFiles(entry));
                }
                if (!Files.isDirectory(entry)) {
                    result.add(entry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    BufferedImage getBufferedImage(File file) throws IOException {
        return ir.readImage(file, "dcraw", "dcraw");

    }

    public void runFITS(String Originals, String fits_results){
        FitsWrap fitsWrap=null;
        try {
            FitsWrap.setFitsHome("../fits-api/fits-0.8.5");
            fitsWrap = FitsWrap.instance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Path> originals = listFiles((new File(Originals)).toPath());
        int i = 0;
        int size = originals.size();
        for (Path original_path : originals) {
            try {
                Document document = fitsWrap.extract(original_path.toFile());
                XMLOutputter xmlOutput = new XMLOutputter();
                xmlOutput.setFormat(Format.getPrettyFormat());
                xmlOutput.output(document, new FileWriter(fits_results + File.separator + original_path.getFileName().toString() + ".xml"));
            }  catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void runFITS(){
        FitsWrap fitsWrap=null;
        try {
            FitsWrap.setFitsHome("../fits-api/fits-0.8.5");
            fitsWrap = FitsWrap.instance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Path> originals = listFiles(folderOriginals.toPath());
        int i = 0;
        int size = originals.size();
        for (Path original_path : originals) {
            try {
                Document document = fitsWrap.extract(original_path.toFile());
                XMLOutputter xmlOutput = new XMLOutputter();
                xmlOutput.setFormat(Format.getPrettyFormat());
                xmlOutput.output(document, new FileWriter(folderFits.toString() + File.separator + original_path.getFileName().toString() + ".xml"));
            }  catch (Exception e) {
                e.printStackTrace();
            }


        }




    }



}
