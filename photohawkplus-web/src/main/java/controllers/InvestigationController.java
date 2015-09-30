package controllers;



import cmd.photohawkplusCmd;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.ImageBean;
import ninja.AssetsController;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.utils.NinjaProperties;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FolderHelper;
import utils.ImageOps;

import java.awt.*;
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
    String folderOriginalsString, folderResultsString,folderTmp, folderTmpImages;
    photohawkplusCmd photohawk;
    public Result start(){
        return Results.html();
    }

    public Result photohawk(){
        folderOriginalsString = ninjaProperties.get("images.original");
        folderResultsString = ninjaProperties.get("images.result");
        folderTmp = FolderHelper.getTempPath();
        folderTmpImages=FolderHelper.getTempPath()+File.separator+"temp_photohawk_images";

        logger.info("A folder with original photos: " + folderOriginalsString);
        logger.info("A folder with result photos: " + folderResultsString);
        logger.info("A folder to store temporary files: " + folderTmp);

        photohawk=new photohawkplusCmd(folderOriginalsString,folderResultsString, folderTmp, folderTmpImages);
        images = photohawk.run();
        index=0;
        return Results.html();
    }

    public Result result(){
        cleanAssets();
        List<ImageBean> list = run_thresholding();

        //List<Map.Entry<SimplePojo, String>> result  = new ArrayList(map.entrySet());
        //return Results.html().render("map", result);

        return Results.html().render("map",list);}

    private List<ImageBean> run_thresholding() {
        ArrayList<ImageBean> result=new ArrayList<>();
        double threshold=find_minimum_valid(images);
        for(ImageBean image: images){
            if (image.getSSIM()<=threshold)
                result.add(image);
        }
        return result;
    }

    private double find_minimum_valid(List<ImageBean> images) {
        double result=0.0;
        for(ImageBean image: images){
            if (image.getSSIM()>result && !image.getIsSimilar()){
                result=image.getSSIM();
            }
        }
        return result;
    }

    public Result investigate(Context ctx) {
        Result result=Results.html();
        if (ctx.getParameter("isSimilar")!=null) {

            images.get(index).setIsSimilar(Boolean.parseBoolean(ctx.getParameter("isSimilar")));
            index++;
        }
        if (index<images.size()){

            ImageBean next=images.get(index);
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
        }
        else{
            return Results.redirect("/result");
        }
    }



    String getDownscaledImage(String fileImageString){
        File file;
        file = new File(fileImageString);
        return FolderHelper.getBaseFilename(file.getName())+"_downscaled.png";
    }



    String copyImageToAssets(String input) throws IOException {
        File file=new File(input);
        String filename=file.getName();
        String fileCopyToString=FolderHelper.getAssetsPath(ninjaProperties.isProd())+File.separator+filename;
        File copyTo=new File(fileCopyToString);
        FileUtils.copyFile(file, copyTo);
        return "/assets/"+filename;
    }

    void cleanAssets(){
        try {
            FileUtils.cleanDirectory(new File(FolderHelper.getAssetsPath(ninjaProperties.isProd())));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
