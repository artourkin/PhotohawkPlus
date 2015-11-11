package controllers;


import cmd.photohawkplusCmd;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.ImageBean;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.utils.NinjaProperties;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Constants;
import utils.FolderHelper;
import utils.PhotoConfigurator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by artur on 21/09/15.
 */

@Singleton
public class InvestigationController {

    @Inject
    NinjaProperties ninjaProperties;
    private final Logger logger = LoggerFactory.getLogger(InvestigationController.class);
    List<ImageBean> images;
    int index;
    photohawkplusCmd cmd;
    PhotoConfigurator configurator=PhotoConfigurator.getConfigurator();
    public Result start() {

        configurator.setProperty(Constants.PATH_PHOTO_ORIGINALS,ninjaProperties.get("images.original"));
        configurator.setProperty(Constants.PATH_PHOTO_RESULTS,ninjaProperties.get("images.result"));
        configurator.setProperty(Constants.PATH_TMP, FolderHelper.getTempPath());
        configurator.setProperty(Constants.PATH_TMP_PHOTO,FolderHelper.getTempPath() + File.separator + "temp_photohawk_images");
        configurator.setProperty(Constants.PATH_FITS_RESULTS,ninjaProperties.get("fits.result"));
        configurator.setProperty(Constants.PATH_FITS_HOME,ninjaProperties.get("fits.home"));
        configurator.setProperty(Constants.WEB_AJAX_STATUS, "The process started.");

        logger.info("A folder with original photos: " + configurator.getProperty(Constants.PATH_PHOTO_ORIGINALS));
        logger.info("A folder with result photos: " + configurator.getProperty(Constants.PATH_PHOTO_RESULTS));
        logger.info("A folder to store temporary files: " + configurator.getProperty(Constants.PATH_TMP));
        logger.info("A folder to store FITS results: " + configurator.getProperty(Constants.PATH_FITS_RESULTS));
        cmd =new photohawkplusCmd();
        return Results.html();
    }

    public static class SimplePOJO {
        public String message;
    }

    public Result photohawkAsync(final Context ctx) {
        if (!cmd.isBusy()) {
            cmd.run();
        }
        final SimplePOJO pojo = new SimplePOJO();
        pojo.message = configurator.getProperty(Constants.WEB_AJAX_STATUS);
        logger.info(pojo.message);
        return Results.json().render(pojo);
    }

    public Result result() {
        cleanAssets();
        List<ImageBean> list = run_thresholding();
        return Results.html().render("map", list);
    }

    private List<ImageBean> run_thresholding() {
        ArrayList<ImageBean> result = new ArrayList<>();
        double threshold = find_minimum_valid(images);
        for (ImageBean image : images) {
            if (image.getSSIM() <= threshold)
                result.add(image);
        }
        return result;
    }

    private double find_minimum_valid(List<ImageBean> images) {
        double result = 0.0;
        for (ImageBean image : images) {
            if (image.getSSIM() > result && !image.getIsSimilar()) {
                result = image.getSSIM();
            }
        }
        return result;
    }

    public Result investigate(Context ctx) {
        Result result = Results.html();
        if (ctx.getParameter("isSimilar") != null) {
            images.get(index).setIsSimilar(Boolean.parseBoolean(ctx.getParameter("isSimilar")));
            index++;
        }
        if (index < images.size()) {

            ImageBean next = images.get(index);
            logger.info("Next image and SSIM value: " + next.toString());
            String original_png_downscaled = null;
            String result_png_downscaled = null;
           // try {
               // original_png_downscaled = ImageOps.downscale(next.getOriginalPNG(), folderTmpImages + File.separator + getDownscaledImage(next.getOriginalPNG()));
               // result_png_downscaled = ImageOps.downscale(next.getResultPNG(), folderTmpImages + File.separator + getDownscaledImage(next.getResultPNG()));
          //  } catch (IOException e) {
          //      e.printStackTrace();
          //  }
            try {
                result.render("original", next.getOriginal());
                result.render("original_png", copyImageToAssets(next.getOriginalPNG()));
                result.render("original_png_downscaled", copyImageToAssets(original_png_downscaled));
                result.render("result_png", copyImageToAssets(next.getResultPNG()));
                result.render("result_png_downscaled", copyImageToAssets(result_png_downscaled));

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        } else {
            return Results.redirect("/result");
        }
    }

    String getDownscaledImage(String fileImageString) {
        File file;
        file = new File(fileImageString);
        return FolderHelper.getBaseFilename(file.getName()) + "_downscaled.png";
    }

    String copyImageToAssets(String input) throws IOException {
        File file = new File(input);
        String filename = file.getName();
        String fileCopyToString = FolderHelper.getAssetsPath(ninjaProperties.isProd()) + File.separator + filename;
        File copyTo = new File(fileCopyToString);
        FileUtils.copyFile(file, copyTo);
        return "/assets/" + filename;
    }

    void cleanAssets() {
        try {
            FileUtils.cleanDirectory(new File(FolderHelper.getAssetsPath(ninjaProperties.isProd())));
            FileUtils.cleanDirectory(new File(configurator.getProperty(Constants.PATH_FITS_RESULTS)));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

/*    public void runPhotohawk() {
        PhotoConfigurator configurator=PhotoConfigurator.getConfigurator();
        configurator.setProperty(Constants.PATH_PHOTO_ORIGINALS,ninjaProperties.get("images.original"));
        configurator.setProperty(Constants.PATH_PHOTO_RESULTS,ninjaProperties.get("images.result"));
        configurator.setProperty(Constants.PATH_TMP, FolderHelper.getTempPath());
        configurator.setProperty(Constants.PATH_TMP_PHOTO,FolderHelper.getTempPath() + File.separator + "temp_photohawk_images");
        configurator.setProperty(Constants.PATH_FITS_RESULTS,ninjaProperties.get("fits.result"));


        logger.info("A folder with original photos: " + configurator.getProperty(Constants.PATH_PHOTO_ORIGINALS));
        logger.info("A folder with result photos: " + configurator.getProperty(Constants.PATH_PHOTO_RESULTS));
        logger.info("A folder to store temporary files: " + configurator.getProperty(Constants.PATH_TMP));
        logger.info("A folder to store FITS results: " + configurator.getProperty(Constants.PATH_FITS_RESULTS));

        isStarted = true;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                images = new ArrayList<>();
                photohawkplusCmd cmd = new photohawkplusCmd();//folderOriginalsString, folderResultsString, folderTmp, folderTmpImages, folderFitsResults);
                status = "Listing the images.";
                cmd.runFITS();

                List<Path> originals = cmd.listFiles((new File(folderOriginalsString)).toPath());
                List<Path> results = cmd.listFiles((new File(folderResultsString)).toPath());
                int i = 0;
                int size = originals.size();
                for (Path original_path : originals) {
                    String original_base = cmd.getBaseFilename(original_path.getFileName().toString());
                    for (Path result_path : results) {
                        String result_base = cmd.getBaseFilename(result_path.getFileName().toString());
                        if (!original_base.isEmpty() && original_base.equals(result_base)) {
                            try {
                                i++;
                                status = "Processing an image " + String.valueOf(i) + " of " + String.valueOf(size - 1) + ".";
                                String original_PNG = (new File(folderTmpImages.toString() + File.separator + original_path.getFileName().toString() + ".png")).toString();
                                String result_PNG = (new File(folderTmpImages.toString() + File.separator + result_path.getFileName().toString() + ".png")).toString();
                                ImageBean image = cmd.calculateSSIM(original_path.toString(), result_path.toString(), original_PNG, result_PNG);
                                images.add(image);

                            } catch (IOException e) {
                                status = e.getMessage();
                            }
                        }

                    }

                }
                status = "";
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isStarted = false;

            }
        });

    }*/

    ;


}

