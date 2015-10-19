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

import com.fasterxml.jackson.databind.util.JSONPObject;
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

    public Result index() {

        return Results.html();
    }


    public Result newBookAjax()
    {
        return Results.html();
    }
    public Result newBookFormPostAjax(Context context, String book)
    {
        System.out.println("Book received: title is " + book);
        return Results.json().render(book);
    }



}
