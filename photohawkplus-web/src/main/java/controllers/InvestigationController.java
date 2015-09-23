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
import java.util.Iterator;
import java.util.List;

/**
 * Created by artur on 21/09/15.
 */

@Singleton
public class InvestigationController {
    @Inject
    NinjaProperties ninjaProperties;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    List<ImageBean> images;
    int index;

    public Result start(){
        return Results.html();
    }

    public Result photohawk(){
        String originals = ninjaProperties.get("images.original");
        String results= ninjaProperties.get("images.result");
        String tmpPath= FolderHelper.getTempPath();

        logger.debug("A folder with original photos: " + originals);
        logger.debug("A folder with result photos: " + results);
        logger.debug("A folder to store temporary files: " + tmpPath);

        photohawkplusCmd photohawkPlus=new photohawkplusCmd(originals,results, tmpPath);
        images = photohawkPlus.run();
        index=0;
        return Results.html();
    }

    public Result investigate(Context ctx) {

        if (ctx.getParameter("isSimilar")!=null) {
            images.get(index).setIsSimilar(Boolean.parseBoolean(ctx.getParameter("isSimilar")));
            index++;
        }
        if (index<images.size()){
            ImageBean next=images.get(index);
            String original_resized = ImageOps.downscale(next.getOriginal_PNG()); //TODO: refactor !!!!!
            String result_resized = ImageOps.downscale(next.getResult_PNG());

            Result result=Results.html();
            result.render("original",imageToAssets(next.getOriginal_PNG()));
            result.render("original_resized",imageToAssets(original_resized));
            result.render("result",imageToAssets(next.getResult_PNG()));
            result.render("result_resized",imageToAssets(result_resized));
            return result;

        }
        else{
            return Results.redirect("/result");

        }
    }

    public Result result(){
        return Results.html();

    }

    String imageToAssets(String input){
        File file=new File(input);
        File copyTo=null;
        if (file.exists()){
            String filename=file.getName();

            try {
                copyTo=new File("src/main/java/assets/images/"+filename);
                FileUtils.copyFile(file, copyTo);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (copyTo!=null) {
            String path = copyTo.getPath();
            if (path.startsWith("src/main/java")) {
                path=path.replace("src/main/java", "");

            }
            return path;
        }
        return input;

    }

}
