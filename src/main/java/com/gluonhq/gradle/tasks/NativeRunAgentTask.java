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
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskAction;
import org.gradle.util.GradleVersion;
import org.openjfx.gradle.JavaFXModule;
import org.openjfx.gradle.JavaFXOptions;
import org.openjfx.gradle.JavaFXPlatform;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class NativeRunAgentTask extends NativeBaseTask {

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
    public NativeRunAgentTask(Project project) {
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

            JavaFXOptions javaFXOptions = project.getExtensions().getByType(JavaFXOptions.class);
            var definedJavaFXModuleNames = new TreeSet<>(javaFXOptions.getModules());
            if (definedJavaFXModuleNames.isEmpty()) {
                throw new GradleException("No JavaFX modules found.");
            }
            final FileCollection classpathWithoutJavaFXJars = execTask.getClasspath().filter(
                    jar -> Arrays.stream(JavaFXModule.values()).noneMatch(javaFXModule ->
                            jar.getName().contains(javaFXModule.getArtifactName()))
            );
            final FileCollection javaFXPlatformJars = execTask.getClasspath().filter(jar ->
                    isJavaFXJar(jar, javaFXOptions.getPlatform()));

            // Remove all JavaFX jars from classpath
            execTask.setClasspath(classpathWithoutJavaFXJars);

            // Define JVM args for command line
            var javaFXModuleJvmArgs = List.of("--module-path", javaFXPlatformJars.getAsPath());
            var jvmArgs = new ArrayList<>(javaFXModuleJvmArgs);
            jvmArgs.add("--add-modules");
            jvmArgs.add(String.join(",", definedJavaFXModuleNames));
            if (GradleVersion.current().compareTo(GradleVersion.version("6.6")) < 0) {
                // Include classpath as JVM arg for Gradle versions lower than 6.6
                jvmArgs.add("-cp");
                jvmArgs.add(classpathWithoutJavaFXJars.getAsPath());
            }

            // set java_home
            execTask.executable(Path.of(graalVMHome.toString(), "bin", "java").toString());

            // set jvmargs
            execTask.getJvmArgs().add(AGENTLIB_NATIVE_IMAGE_AGENT_STRING);
            execTask.getJvmArgs().addAll(jvmArgs);

            // run
            execTask.exec();
        } catch (Exception e) {
            throw new GradleException("RunAgent failure: " + e);
        }
    }

    private Path getGraalHome() {
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

    private static boolean isJavaFXJar(File jar, JavaFXPlatform platform) {
        return jar.isFile() &&
                Arrays.stream(JavaFXModule.values()).anyMatch(javaFXModule ->
                        javaFXModule.compareJarFileName(platform, jar.getName()) ||
                                javaFXModule.getModuleJarFileName().equals(jar.getName()));
    }
}
