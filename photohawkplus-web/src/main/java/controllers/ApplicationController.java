/**
 * Copyright (C) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers;

import ninja.*;
import ninja.params.Param;

import com.google.inject.Singleton;
import com.google.inject.Inject;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaProperties;
import ninja.utils.NinjaPropertiesImpl;
import org.apache.commons.io.FileUtils;
import utils.CSVReader;
import dao.*;
import utils.ImageOps;

import java.io.File;
import java.io.IOException;
import java.util.List;



@Singleton
public class ApplicationController {
    @Inject
    NinjaProperties ninjaProperties;
    public Result index(Context ctx) {

        if (ctx.getParameter("isSimilar")!=null) {
            String original = ctx.getSession().get("original");
            String result = ctx.getSession().get("result");
            String isSimilar = ctx.getParameter("isSimilar");


            saveResults(original, isSimilar);
        }

        ImageBean next=getNextImage();


        String original_resized = ImageOps.downscale(next.getOriginal_PNG());
        String result_resized = ImageOps.downscale(next.getResult_PNG());

        Result result=Results.html();
        result.render("original",imageToAssets(next.getOriginal_PNG()));
        result.render("original_resized",imageToAssets(original_resized));
        result.render("result",imageToAssets(next.getResult_PNG()));
        result.render("result_resized",imageToAssets(result_resized));
        return result;
    }
    List<ImageBean> result_images;

    String imageToAssets(String input){
        File file=new File(input);
        File copyTo=null;
        if (file.exists()){
            String filename=file.getName();

            try {
                copyTo=new File("src/main/java/assets/images/"+filename);
                FileUtils.copyFile(file,copyTo);
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

    private void saveResults(String original, String isSimilar) {
        ImageBean image=input_images.get(0);
        image.setIsSimilar(Boolean.parseBoolean(isSimilar));
        result_images.add(image);
    }

    List<ImageBean> input_images;
    private ImageBean getNextImage() {
        ImageBean result=null;
        if (input_images==null){
            try {
                input_images= CSVReader.readWithCsvBeanReader("src/test/java/resources/images.csv");
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        if (input_images!=null && input_images.size()>0)
            result= input_images.remove(0);
        return  result;
    }

    public Result helloWorldJson() {
        
        SimplePojo simplePojo = new SimplePojo();
        simplePojo.content = "Hello <b>World!</b> Hello Json!";


        return Results.json().render(simplePojo);

    }
    
    public static class SimplePojo {

        public String content;
        
    }
}
