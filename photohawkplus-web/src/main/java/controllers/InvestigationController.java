package controllers;



import cmd.photohawkplusCmd;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.Result;
import ninja.Results;
import ninja.utils.NinjaProperties;
import utils.FolderHelper;
/**
 * Created by artur on 21/09/15.
 */

@Singleton
public class InvestigationController {
    @Inject
    NinjaProperties ninjaProperties;
    void runPhotohawk(){
        String s = ninjaProperties.get("images.original");
        String t= ninjaProperties.get("images.result");
        String u= FolderHelper.getAssetsPath();

        photohawkplusCmd photohawkPlus=new photohawkplusCmd(ninjaProperties.get("images.original"),ninjaProperties.get("images.result"), FolderHelper.getAssetsPath());
        photohawkPlus.run();
        //return Results.html();
    }


    public Result investigate(){
        runPhotohawk();
        return Results.html();
    }

}
