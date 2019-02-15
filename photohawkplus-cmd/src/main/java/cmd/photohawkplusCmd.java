package cmd;

import wraps.C3POWrap;
import wraps.FITSWrap;
import wraps.PhotohawkWrap;
import dao.ImageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Constants;
import utils.PhotoConfigurator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class photohawkplusCmd {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ScheduledExecutorService executorService = Executors
            .newSingleThreadScheduledExecutor();
    List<ImageBean> images;
    C3POWrap c3poWrap;
    PhotohawkWrap photohawkWrap;
    FITSWrap fitsWrap;
    Boolean isBusy;
    PhotoConfigurator cfg = PhotoConfigurator.getConfigurator();

    public photohawkplusCmd() {
        isBusy = false;
        try {
            Files.createDirectories(Paths.get(cfg.getProperty(Constants.PATH_TMP_PHOTO))).toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Please specify the following:/path/to/originals /path/to/results /path/to/tmp_results /path/to/tmp_image_results");
            return;
        }
    }

    public List<ImageBean> getImages(){
        if (photohawkWrap!=null)
            return photohawkWrap.getImages();
        return new ArrayList<ImageBean>();
    }
   // public List<ImageBean> getSamples(){
        //if (c3poWrap!=null)
           // return c3poWrap.getSamples();
   // }

    public void run() {

        isBusy = true;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                images = new ArrayList<>();
                fitsWrap = new FITSWrap();
                fitsWrap.execute();
                cfg.setProperty(Constants.WEB_AJAX_STATUS, cfg.getProperty( Constants.PATH_FITS_RESULTS));
                c3poWrap = new C3POWrap("localhost", "27017", "c3po", cfg.getProperty( Constants.PATH_FITS_RESULTS));
                c3poWrap.execute();
                photohawkWrap = new PhotohawkWrap();
                photohawkWrap.execute();

                cfg.setProperty(Constants.WEB_AJAX_STATUS, "");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                shutdownExecutor();
            }
        });
    }

    public void run_serial() {
        isBusy = true;
        photohawkWrap = new PhotohawkWrap();
        fitsWrap = new FITSWrap();
        c3poWrap = new C3POWrap("localhost", "27017", "c3po", cfg.getProperty( Constants.PATH_FITS_RESULTS));
        c3poWrap.execute();

        images = new ArrayList<>();

        //fitsWrap.execute();
        c3poWrap.execute();
        List<String> samples = c3poWrap.getSamples();
        photohawkWrap.execute();

        cfg.setProperty(Constants.WEB_AJAX_STATUS, "");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        shutdownExecutor();
    }



    public void shutdownExecutor() {
        isBusy = false;
    }

    public Boolean isBusy(){
        return isBusy;
    }


}
