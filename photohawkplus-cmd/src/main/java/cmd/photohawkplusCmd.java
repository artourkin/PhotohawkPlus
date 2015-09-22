package cmd;

import at.ac.tuwien.photohawk.commandline.util.ImageReader;
import at.ac.tuwien.photohawk.evaluation.colorconverter.StaticColor;
import at.ac.tuwien.photohawk.evaluation.operation.TransientOperation;
import at.ac.tuwien.photohawk.evaluation.qa.SsimQa;
import dao.ImageBean;
import utils.CSVWriter;

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

    }
    public void run() {
        List<Path> originals=listFiles(originals_folder.toPath());
        List<Path> results=listFiles(results_folder.toPath());
        for (Path original: originals){
            String original_base=getBaseFilename(original.getFileName().toString());
            for (Path result: results){
                String result_base=getBaseFilename(result.getFileName().toString());
                if (!original_base.isEmpty() && original_base.equals(result_base)){
                    try {
                        BufferedImage bImage1 = getBufferedImage(original.toFile());
                        BufferedImage bImage2 = getBufferedImage(result.toFile());
                        double SSIM=ssimQa.evaluate(bImage1, bImage2).getResult().getChannelValue(0);
                        String original_PNG=saveImage(original, bImage1);
                        String result_PNG=saveImage(result, bImage2);
                        bImage1.flush();
                        bImage2.flush();
                        ImageBean imageBean=new ImageBean(SSIM, true,original.toString(),result.toString(),original_PNG, result_PNG);
                        csvWriter.write(imageBean);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

            }
        }
    csvWriter.destroy();
    }
    private String saveImage(Path original, BufferedImage bImage1) throws IOException {
        File file=new File(tif_folder.toString() + File.separator + original.getFileName().toString()+".png");
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

    private double evaluate(File left, File right){
        String e = null;
        Float result=null;
        try {
            e = this.ir.determineReadMode(left, "dcraw");
            String rightMode = this.ir.determineReadMode(right, "dcraw");
            BufferedImage leftImg = this.ir.readImage(left, e, rightMode);
            BufferedImage rightImg = this.ir.readImage(right, rightMode, e);



            TransientOperation<Float, StaticColor> op = this.ssimQa.evaluate(leftImg, rightImg);
            result = op.getResult().getChannelValue(0);
            File leftImg_png=new File(left.getName()+".png");
            File rightImg_png=new File(right.getName()+".png");
            ImageIO.write(leftImg, "png", leftImg_png);
            ImageIO.write(rightImg, "png", rightImg_png);

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if (result !=null)
            return result.doubleValue();
        return -1;

    }

}
