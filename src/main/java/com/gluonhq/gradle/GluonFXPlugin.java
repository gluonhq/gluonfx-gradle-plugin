/*
 * Copyright (c) 2019, 2021, Gluon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.gradle;

import com.gluonhq.gradle.tasks.ClientNativeBuild;
import com.gluonhq.gradle.tasks.ClientNativeCompile;
import com.gluonhq.gradle.tasks.ClientNativeInstall;
import com.gluonhq.gradle.tasks.ClientNativeLink;
import com.gluonhq.gradle.tasks.ClientNativePackage;
import com.gluonhq.gradle.tasks.ClientNativeRun;
import com.gluonhq.gradle.tasks.ClientNativeRunAgent;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

public class GluonFXPlugin implements Plugin<Project> {

    public static final String NATIVE_COMPILE_TASK_NAME = "nativeCompile";
    public static final String NATIVE_LINK_TASK_NAME = "nativeLink";
    public static final String NATIVE_RUN_TASK_NAME = "nativeRun";
    public static final String NATIVE_BUILD_TASK_NAME = "nativeBuild";
    public static final String NATIVE_PACKAGE_TASK_NAME = "nativePackage";
    public static final String NATIVE_INSTALL_TASK_NAME = "nativeInstall";
    public static final String NATIVE_RUN_AGENT_TASK_NAME = "nativeRunAgent";

    private static final String CONFIGURATION_CLIENT = "client";

    private ObjectFactory objectFactory;
	private Project project;

    @Inject
    GluonFXPlugin(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Override
    public void apply(Project project) {
    	this.project = project;

        project.getConfigurations().create(CONFIGURATION_CLIENT);

        project.getExtensions().create("gluonFX", ClientExtension.class, project, objectFactory);

        createTask(NATIVE_COMPILE_TASK_NAME, ClientNativeCompile.class, "Native AOT compilation of application.");
        createTask(NATIVE_LINK_TASK_NAME, ClientNativeLink.class, "Native link of application.");
        createTask(NATIVE_BUILD_TASK_NAME, ClientNativeBuild.class, "Combines AOT compilation and link of application.");
        createTask(NATIVE_RUN_TASK_NAME, ClientNativeRun.class, "Runs the native application in the target platform.");
        createTask(NATIVE_PACKAGE_TASK_NAME, ClientNativePackage.class, "Packages the native application for the target platform.");
        createTask(NATIVE_INSTALL_TASK_NAME, ClientNativeInstall.class, "Installs the packaged native application on the target platform.");
        createTask(NATIVE_RUN_AGENT_TASK_NAME, ClientNativeRunAgent.class, "Runs tracing agent to generate config files");
    }
    
    private void createTask(String name, Class<? extends Task> taskClass, String decription) {
    	Task t = project.getTasks().create(name, taskClass, project);
    	t.setGroup("Gluon client");
    	t.setDescription(decription);
    }
}
