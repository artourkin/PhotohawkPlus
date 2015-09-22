package utils;

import com.google.inject.Inject;
import ninja.utils.NinjaProperties;

/**
 * Created by artur on 21/09/15.
 */
public class FolderHelper {
    @Inject
    static NinjaProperties ninjaProperties;

    public static String getAssetsPath(){

        if (!ninjaProperties.isProd()){
            return "src/main/java/assets/";
        }
        return "/assets";
    }
}
