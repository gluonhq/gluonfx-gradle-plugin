/*
 * Copyright (c) 2021, Gluon
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
package com.gluonhq.gradle.tasks;

import com.gluonhq.gradle.ClientExtension;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ClientNativeRunAgent extends ClientNativeBase {

    private static final String CONFIG_JAVAFX_RUN_TASK = "configJavafxRun";

    private static final String AGENTLIB_NATIVE_IMAGE_AGENT_STRING =
            "-agentlib:native-image-agent=access-filter-file=src/main/resources/META-INF/native-image/filter-file.json,config-merge-dir=src/main/resources/META-INF/native-image";

    private static final List<String> AGENTLIB_EXCLUSION_RULES = Arrays.asList(
            "com.sun.glass.ui.mac.*", "com.sun.prism.es2.MacGLFactory",
            "com.sun.glass.ui.gtk.*", "com.sun.prism.es2.X11GLFactory",
            "com.gluonhq.attach.**"
    );

    private final ClientExtension clientExtension;

    @Inject
    public ClientNativeRunAgent(Project project) {
        super(project);
        clientExtension = project.getExtensions().getByType(ClientExtension.class);

        Task javafxRun = project.getTasks().findByName(CONFIG_JAVAFX_RUN_TASK);
        if (javafxRun == null) {
            throw new GradleException("javafxplugin:" + CONFIG_JAVAFX_RUN_TASK + " task not found.");
        }
        this.dependsOn(javafxRun.getPath());
    }

    @TaskAction
    public void action() {
        getProject().getLogger().info("ClientNativeRunAgent action");

        Path graalVMHome = getGraalHome();

        try {
            new ConfigBuild(project).createSubstrateDispatcher();
        } catch (Exception e) {
            throw new GradleException("Error creating Substrate Dispatcher: " + e);
        }

        try {
            // folder
            Path path = Path.of(project.getProjectDir().getAbsolutePath(), "src", "main", "resources", "META-INF", "native-image");
            if (Files.exists(path)) {
                // TODO: Delete files
                // otherwise it keeps merging results from different runs
                // and config files might get outdated.
            } else {
                Files.createDirectories(path);
            }

            // Create filter file to exclude platform classes
            try {
                createFilterFile(path.resolve("filter-file.json").toString());
            } catch (IOException e) {
                throw new GradleException("Error generating agent filter", e);
            }

            JavaExec execTask = (JavaExec) project.getTasks().findByName(ApplicationPlugin.TASK_RUN_NAME);
            if (execTask == null) {
                throw new GradleException("Run task not found.");
            }
            // collect all jvmArgs after :configJavafxRun
            List<String> args = execTask.getAllJvmArgs();

            // set java_home
            execTask.executable(Path.of(graalVMHome.toString(), "bin", "java").toString());

            // set jvmargs
            execTask.getJvmArgs().add(AGENTLIB_NATIVE_IMAGE_AGENT_STRING);
            execTask.getJvmArgs().addAll(args);

            // run
            execTask.exec();
        } catch (Exception e) {
            throw new GradleException("RunAgent failure: " + e);
        }
    }

    Path getGraalHome() {
        String graalvmHome = clientExtension.getGraalvmHome();
        if (graalvmHome == null) {
            graalvmHome = System.getenv("GRAALVM_HOME");
        }
        if (graalvmHome == null) {
            throw new GradleException("GraalVM installation directory not found." +
                    " Either set GRAALVM_HOME as an environment variable or" +
                    " set graalvmHome in the client-plugin configuration");
        }
        return Path.of(graalvmHome);
    }

    private void createFilterFile(String agentFilter) throws IOException {
        File agentDirFilter = new File(agentFilter);
        if (agentDirFilter.exists()) {
            agentDirFilter.delete();
        }
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(agentDirFilter)))) {
            bw.write("{ \"rules\": [\n");
            boolean ruleHasBeenWritten = false;
            for (String rule : AGENTLIB_EXCLUSION_RULES) {
                if (ruleHasBeenWritten) {
                    bw.write(",\n");
                } else {
                    ruleHasBeenWritten = true;
                }
                bw.write("    {\"excludeClasses\" : \"" + rule + "\"}");
            }
            bw.write("\n  ]\n");
            bw.write("}\n");
        }
    }
}
