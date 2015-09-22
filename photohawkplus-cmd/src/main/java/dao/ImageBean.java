package dao;

/**
 * Created by artur on 16/09/15.
 */
public class ImageBean {
    public String getOriginal() {
        return Original;
    }

    public void setOriginal(String original) {
        Original = original;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public String getOriginal_PNG() {
        return Original_PNG;
    }

    public void setOriginal_PNG(String original_PNG) {
        Original_PNG = original_PNG;
    }

    public String getResult_PNG() {
        return Result_PNG;
    }

    public void setResult_PNG(String result_PNG) {
        Result_PNG = result_PNG;
    }

    public Double getSSIM() {
        return SSIM;
    }

    public void setSSIM(Double SSIM) {
        this.SSIM = SSIM;
    }
    public ImageBean(Double SSIM, Boolean isSimilar,  String Original,String Result,String Original_PNG,String Result_PNG){
        this.SSIM=SSIM;
        this.isSimilar= isSimilar;
        this.Original=Original;
        this.Result=Result;
        this.Original_PNG = Original_PNG;
        this.Result_PNG = Result_PNG;
    }
    public ImageBean(){}

    String Original, Result, Original_PNG, Result_PNG;
    Double SSIM;

    public Boolean getIsSimilar() {
        return isSimilar;
    }

    public void setIsSimilar(Boolean isSimilar) {
        this.isSimilar = isSimilar;
    }

    Boolean isSimilar;

}
