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
import utils.FolderHelper;
import utils.ImageOps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
    String folderOriginalsString, folderResultsString, folderTmp, folderTmpImages;

    photohawkplusCmd photohawk;

    public Result start() {
        return Results.html();
    }

    public Result photohawk() {
        folderOriginalsString = ninjaProperties.get("images.original");
        folderResultsString = ninjaProperties.get("images.result");
        folderTmp = FolderHelper.getTempPath();
        folderTmpImages = FolderHelper.getTempPath() + File.separator + "temp_photohawk_images";

        logger.info("A folder with original photos: " + folderOriginalsString);
        logger.info("A folder with result photos: " + folderResultsString);
        logger.info("A folder to store temporary files: " + folderTmp);

        photohawk = new photohawkplusCmd(folderOriginalsString, folderResultsString, folderTmp, folderTmpImages);
        images = photohawk.run();
        index = 0;

        return Results.html().render("message", "test");
    }

    public static class SimplePOJO {
        public String message;
    }


    public Result photohawkAsync(final Context ctx) {

        if (!isStarted()) {
            runPhotohawk();
        }
        logger.info("New async request accepted");
        //for(Map.Entry<String, List<String>> entry: headers.entrySet()){
        //    logger.info(entry.getKey()+" : " + entry.getValue());
        // }
        //logger.info("context_timeout : " + ctx.getParameter("timeout"));

        final SimplePOJO pojo = new SimplePOJO();
        pojo.message = getStatus();
        logger.info(pojo.message);
        return Results.json().render(pojo);
    }







    /*public Result photohawkAsync_(Context context) {
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        folderOriginalsString = ninjaProperties.get("images.original");
        folderResultsString = ninjaProperties.get("images.result");
        folderTmp = FolderHelper.getTempPath();
        folderTmpImages=FolderHelper.getTempPath()+File.separator+"temp_photohawk_images";
        final List<ImageBean> imageResults=new ArrayList<>();

        logger.info("A folder with original photos: " + folderOriginalsString);
        logger.info("A folder with result photos: " + folderResultsString);
        logger.info("A folder to store temporary files: " + folderTmp);


        //json.append("message", "ahahaha");
        SimplePOJO pojo=new SimplePOJO();
        pojo.message = "It is a final countdown:";

        for (int i = 0; i < 5; i++) {
            pojo.message += String.valueOf(i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info(pojo.message);
            context.returnResultAsync(Results.json().render(pojo));
        }


       // return Results.async();
        // ctx.setAttribute("message", "Processing image: ");
        // ctx.returnResultAsync(Results.json().render("message Processing image: " ));


        photohawk=new photohawkplusCmd(folderOriginalsString,folderResultsString, folderTmp, folderTmpImages);
        List<Path> originals=photohawk.listFiles((new File(folderOriginalsString)).toPath());
        List<Path> results = photohawk.listFiles((new File(folderResultsString)).toPath());
        for (Path original_path: originals){
            String original_base=photohawk.getBaseFilename(original_path.getFileName().toString());
            for (Path result_path: results){
                String result_base=photohawk.getBaseFilename(result_path.getFileName().toString());
                if (!original_base.isEmpty() && original_base.equals(result_base)){
                    try {
                        String original_PNG=(new File(folderTmpImages.toString() + File.separator + original_path.getFileName().toString()+".png")).toString();
                        String result_PNG=(new File(folderTmpImages.toString() + File.separator + result_path.getFileName().toString()+".png")).toString();
                        ImageBean image= photohawk.calculateSSIM(original_path.toString(), result_path.toString(), original_PNG, result_PNG);
                        imageResults.add(image);
                        logger.info("Processing image: " + original_path.getFileName().toString());

                        //Results.async().json().render("message","test");
                        // ctx.returnResultAsync(Results.html().render(ctx));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }


//        executorService.schedule(new Runnable() {
//            @Override
//            public void run() {
//                photohawk=new photohawkplusCmd(folderOriginalsString,folderResultsString, folderTmp, folderTmpImages);
//                List<Path> originals=photohawk.listFiles((new File(folderOriginalsString)).toPath());
//                List<Path> results = photohawk.listFiles((new File(folderResultsString)).toPath());
//                for (Path original_path: originals){
//                    String original_base=photohawk.getBaseFilename(original_path.getFileName().toString());
//                    for (Path result_path: results){
//                        String result_base=photohawk.getBaseFilename(result_path.getFileName().toString());
//                        if (!original_base.isEmpty() && original_base.equals(result_base)){
//                            try {
//                                String original_PNG=(new File(folderTmpImages.toString() + File.separator + original_path.getFileName().toString()+".png")).toString();
//                                String result_PNG=(new File(folderTmpImages.toString() + File.separator + result_path.getFileName().toString()+".png")).toString();
//                                ImageBean image= photohawk.calculateSSIM(original_path.toString(), result_path.toString(), original_PNG, result_PNG);
//                                imageResults.add(image);
//                                ctx.returnResultAsync(Results.json().render("Processing image: " + original_path.getFileName().toString()));
//                                //ctx.setAttribute("message", "Processing image: " + original_path.getFileName().toString());
//
//                                //Results.async().json().render("message","test");
//                               // ctx.returnResultAsync(Results.html().render(ctx));
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }
//
//                }
//
//            }
//        }, 30000, TimeUnit.MILLISECONDS);
        //return Results.json().render("message","testasd");
    }*/


    public Result result() {
        cleanAssets();
        List<ImageBean> list = run_thresholding();

        //List<Map.Entry<SimplePojo, String>> result  = new ArrayList(map.entrySet());
        //return Results.html().render("map", result);

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
            try {
                original_png_downscaled = ImageOps.downscale(next.getOriginalPNG(), folderTmpImages + File.separator + getDownscaledImage(next.getOriginalPNG()));
                result_png_downscaled = ImageOps.downscale(next.getResultPNG(), folderTmpImages + File.separator + getDownscaledImage(next.getResultPNG()));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private final ScheduledExecutorService executorService = Executors
            .newSingleThreadScheduledExecutor();

    private String status = "The process started.";
    private Boolean isStarted = false;

    public String getStatus() {
        return status;
    }

    public Boolean isStarted() {
        return isStarted;
    }

    public void shutdownExecutor() {
        isStarted = false;
    }

    public void runPhotohawk() {
        folderOriginalsString = ninjaProperties.get("images.original");
        folderResultsString = ninjaProperties.get("images.result");
        folderTmp = FolderHelper.getTempPath();
        folderTmpImages = FolderHelper.getTempPath() + File.separator + "temp_photohawk_images";

        logger.info("A folder with original photos: " + folderOriginalsString);
        logger.info("A folder with result photos: " + folderResultsString);
        logger.info("A folder to store temporary files: " + folderTmp);

        isStarted = true;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                images = new ArrayList<>();
                photohawkplusCmd photohawk = new photohawkplusCmd(folderOriginalsString, folderResultsString, folderTmp, folderTmpImages);
                status = "Listing the images.";
                List<Path> originals = photohawk.listFiles((new File(folderOriginalsString)).toPath());
                List<Path> results = photohawk.listFiles((new File(folderResultsString)).toPath());
                int i = 0;
                int size = originals.size();
                for (Path original_path : originals) {
                    String original_base = photohawk.getBaseFilename(original_path.getFileName().toString());
                    for (Path result_path : results) {
                        String result_base = photohawk.getBaseFilename(result_path.getFileName().toString());
                        if (!original_base.isEmpty() && original_base.equals(result_base)) {
                            try {
                                i++;
                                status = "Processing an image " + String.valueOf(i) + " of " + String.valueOf(size - 1) + ".";
                                String original_PNG = (new File(folderTmpImages.toString() + File.separator + original_path.getFileName().toString() + ".png")).toString();
                                String result_PNG = (new File(folderTmpImages.toString() + File.separator + result_path.getFileName().toString() + ".png")).toString();
                                ImageBean image = photohawk.calculateSSIM(original_path.toString(), result_path.toString(), original_PNG, result_PNG);
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

    }

    ;


}

