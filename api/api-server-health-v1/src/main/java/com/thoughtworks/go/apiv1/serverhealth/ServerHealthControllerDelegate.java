/*
 * Copyright 2018 ThoughtWorks, Inc.
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

package com.thoughtworks.go.apiv1.serverhealth;


import com.google.gson.GsonBuilder;
import com.thoughtworks.go.api.ApiController;
import com.thoughtworks.go.api.ApiVersion;
import com.thoughtworks.go.spark.Routes;
import spark.Request;
import spark.Response;

import java.util.Collections;

import static spark.Spark.*;

public class ServerHealthControllerDelegate extends ApiController {
    private static String HEALTH_OK = new GsonBuilder().setPrettyPrinting().create().toJson(Collections.singletonMap("health", "OK")) + "\n";

    public ServerHealthControllerDelegate() {
        super(ApiVersion.v1);
    }

    @Override
    public String controllerBasePath() {
        return Routes.ServerHealth.BASE;
    }

    @Override
    public void setupRoutes() {
        path(controllerBasePath(), () -> {
            before("", this::setContentType);
            get("", this::show);
        });
    }

    public String show(Request request, Response response) {
        return HEALTH_OK;
    }
}