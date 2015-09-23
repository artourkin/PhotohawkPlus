package dao;

/**
 * Created by artur on 16/09/15.
 */
public class ImageBean {

    String original, result, originalPNG, resultPNG;
    Double SSIM;
    Boolean isSimilar;

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getOriginalPNG() {
        return originalPNG;
    }

    public void setOriginalPNG(String originalPNG) {
        this.originalPNG = originalPNG;
    }

    public String getResultPNG() {
        return resultPNG;
    }

    public void setResultPNG(String resultPNG) {
        this.resultPNG = resultPNG;
    }

    public Double getSSIM() {
        return SSIM;
    }

    public void setSSIM(Double SSIM) {
        this.SSIM = SSIM;
    }
    public ImageBean(Double SSIM, Boolean Is_similar,  String Original,String Result,String Original_PNG,String Result_PNG){
        this.SSIM=SSIM;
        this.isSimilar= Is_similar;
        this.original =Original;
        this.result =Result;
        this.originalPNG = Original_PNG;
        this.resultPNG = Result_PNG;
    }
    public ImageBean(){}


    public Boolean getIsSimilar() {
        return isSimilar;
    }

    public void setIsSimilar(Boolean isSimilar) {
        this.isSimilar = isSimilar;
    }



}
