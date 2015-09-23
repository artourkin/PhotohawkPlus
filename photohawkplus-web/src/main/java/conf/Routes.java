/**
 * Copyright (C) 2012 the original author or authors.
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

package conf;


import controllers.InvestigationController;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import controllers.ApplicationController;
import ninja.utils.NinjaConstant;

public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {
        configure();

        router.GET().route("/").with(ApplicationController.class, "index");
        router.GET().route("/start").with(InvestigationController.class,"start");
        router.GET().route("/photohawk").with(InvestigationController.class,"photohawk");
        router.GET().route("/investigate").with(InvestigationController.class,"investigate");
        router.GET().route("/result").with(InvestigationController.class,"result");
       // router.GET().route("/investigate/{ID}").with(InvestigationController.class,"run");
 
        ///////////////////////////////////////////////////////////////////////
        // Assets (pictures / javascript)
        ///////////////////////////////////////////////////////////////////////    
        router.GET().route("/assets/webjars/{fileName: .*}").with(AssetsController.class, "serveWebJars");
        router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, "serveStatic");
        
        ///////////////////////////////////////////////////////////////////////
        // Index / Catchall shows index page
        ///////////////////////////////////////////////////////////////////////
       // router.GET().route("/.*").with(ApplicationController.class, "index");
    }


    private void configure(){
        System.setProperty(NinjaConstant.MODE_KEY_NAME, NinjaConstant.MODE_DEV);
    }

}
