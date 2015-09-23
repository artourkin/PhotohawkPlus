package cmd;

import at.ac.tuwien.photohawk.commandline.util.ImageReader;
import at.ac.tuwien.photohawk.evaluation.colorconverter.StaticColor;
import at.ac.tuwien.photohawk.evaluation.operation.TransientOperation;
import at.ac.tuwien.photohawk.evaluation.qa.SsimQa;
import dao.ImageBean;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CSVWriter;
import utils.FolderHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class photohawkplusCmd {
    SsimQa ssimQa;
    ImageReader ir;
    File originals_folder,results_folder, tmp_results_folder;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    Path tif_folder;
    CSVWriter csvWriter;
    List<ImageBean> imageBeans;
    public static void main(String[] args) {
        if (args.length !=3) {
            System.out.println("Please specify the following:/path/to/originals /path/to/results /path/to/tmp_results");
            return;
        }
        photohawkplusCmd m=new photohawkplusCmd(args[0],args[1],args[2]);

        if (!m.originals_folder.isDirectory() || !m.results_folder.isDirectory())
            return;
        m.init();
        m.run();
    }
    public photohawkplusCmd(String path_to_originals, String path_to_results, String path_to_tmp){
        originals_folder=new File(path_to_originals);
        results_folder=new File(path_to_results);
        tmp_results_folder=new File(path_to_tmp);
        init();
    }

    private void init() {
        logger.debug("Initialising SSIM calculator...");
        ir = new ImageReader("dcraw.ssim");
        imageBeans=new ArrayList<>();
        ssimQa = new SsimQa();
        ssimQa.numThreads(4);
        File dirToCreate=new File(tmp_results_folder.getAbsolutePath() +File.separator+"images");
        try {
            tif_folder = Files.createDirectories(dirToCreate.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        csvWriter=new CSVWriter(new File(tmp_results_folder.toString()+File.separator+"images.csv"));
        System.out.println("Initialization done.");

    }
    public Double calculateSSIM(String fileString1, String fileString2, String fileString1_png, String fileString2_png) throws IOException {
        System.out.println("Calculating metrics for " + fileString1+ " and " + fileString2);
        File file1 = new File(fileString1);
        File file2 = new File(fileString2);
        BufferedImage bImage1 = getBufferedImage(file1);
        BufferedImage bImage2 = getBufferedImage(file2);
        double SSIM=ssimQa.evaluate(bImage1, bImage2).getResult().getChannelValue(0);
        saveImage(bImage1,fileString1_png);
        saveImage(bImage2,fileString2_png);
        return SSIM;
    }
    public Double calculateSSIM(String fileString1, String fileString2) throws IOException {
        System.out.println("Calculating metrics for " + fileString1+ " and " + fileString2);
        File file1 = new File(fileString1);
        File file2 = new File(fileString2);
        BufferedImage bImage1 = getBufferedImage(file1);
        BufferedImage bImage2 = getBufferedImage(file2);
        double SSIM=ssimQa.evaluate(bImage1, bImage2).getResult().getChannelValue(0);
        String saveTo1=FolderHelper.getTempPath()+File.separator+file1.getName().toString()+".png";
        String saveTo2=FolderHelper.getTempPath()+File.separator+file2.getName().toString()+".png";
        saveImage(bImage1, saveTo1);
        saveImage(bImage2,saveTo2);
        return SSIM;
    }




    public List<ImageBean> run() {
        List<ImageBean> result=new ArrayList<>();
        List<Path> originals=listFiles(originals_folder.toPath());
        List<Path> results=listFiles(results_folder.toPath());
        for (Path original_path: originals){
            String original_base=getBaseFilename(original_path.getFileName().toString());
            for (Path result_path: results){
                String result_base=getBaseFilename(result_path.getFileName().toString());
                if (!original_base.isEmpty() && original_base.equals(result_base)){
                    try {
                        System.out.println("Calculating metrics for " + original_path.getFileName());
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
    csvWriter.destroy();

        return result;
    }
    private String saveImage(Path original, BufferedImage bImage1) throws IOException {
        File file=new File(tif_folder.toString() + File.separator + original.getFileName().toString()+".png");
        ImageIO.write(bImage1, "png", file);
        return file.getAbsolutePath();
    }

    private String saveImage(BufferedImage bImage1, String pathToSave) throws IOException {
        File file=new File(pathToSave);
        ImageIO.write(bImage1, "png", file);
        return file.getAbsolutePath();
    }

    String getBaseFilename(String filepath){
        return filepath.split("\\.(?=[^\\.]+$)")[0];
    }

    List<Path> listFiles(Path path) {
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



}
