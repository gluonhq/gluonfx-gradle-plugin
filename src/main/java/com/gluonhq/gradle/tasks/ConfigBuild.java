/*
 * Copyright (c) 2019, Gluon
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
import com.gluonhq.substrate.Constants;
import com.gluonhq.substrate.SubstrateDispatcher;
import com.gluonhq.substrate.model.IosSigningConfiguration;
import com.gluonhq.substrate.model.ProjectConfiguration;
import com.gluonhq.substrate.model.Triplet;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.compile.JavaCompile;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

class ConfigBuild {

    private ProjectConfiguration clientConfig;
    private final Project project;
    private final ClientExtension clientExtension;

    ConfigBuild(Project project) {
        this.project = project;

        clientExtension = project.getExtensions().getByType(ClientExtension.class);
    }

    void configClient() {
        clientConfig = new ProjectConfiguration();
        clientConfig.setJavaStaticSdkVersion(clientExtension.getJavaStaticSdkVersion());
        clientConfig.setJavafxStaticSdkVersion(clientExtension.getJavafxStaticSdkVersion());

        String osname = System.getProperty("os.name", "Mac OS X").toLowerCase(Locale.ROOT);
        Triplet hostTriplet;
        if (osname.contains("mac")) {
            hostTriplet = new Triplet(Constants.Profile.MACOS);
        } else if (osname.contains("nux")) {
            hostTriplet = new Triplet(Constants.Profile.LINUX);
        } else {
            throw new RuntimeException("OS " + osname + " not supported");
        }

        Triplet targetTriplet;
        String target = clientExtension.getTarget().toLowerCase(Locale.ROOT);
        switch (target) {
            case Constants.TARGET_HOST:
                targetTriplet = hostTriplet;
                break;
            case Constants.TARGET_IOS:
                targetTriplet = new Triplet(Constants.Profile.IOS);
                break;
            case Constants.TARGET_IOS_SIM:
                targetTriplet = new Triplet(Constants.Profile.IOS_SIM);
                break;
            default:
                throw new RuntimeException("No valid target found for " + target);
        }
        clientConfig.setTarget(targetTriplet);

        clientConfig.setBackend(clientExtension.getBackend().toLowerCase(Locale.ROOT));
        clientConfig.setBundlesList(clientExtension.getBundlesList());
        clientConfig.setResourcesList(clientExtension.getResourcesList());
        clientConfig.setDelayInitList(clientExtension.getDelayInitList());
        clientConfig.setJniList(clientExtension.getJniList());
        clientConfig.setReflectionList(clientExtension.getReflectionList());
        clientConfig.setRuntimeArgsList(clientExtension.getRuntimeArgsList());
        clientConfig.setReleaseSymbolsList(clientExtension.getReleaseSymbolsList());

        clientConfig.setMainClassName((String) project.getProperties().get("mainClassName"));
        clientConfig.setAppName(project.getName());

        List<Path> classPath = getClassPathFromSourceSets();
        clientConfig.setUseJavaFX(classPath.stream().anyMatch(f -> f.getFileName().toString().contains("javafx")));
        clientConfig.setGraalPath(getGraalHome().get());

        clientConfig.setLlcPath(clientExtension.getLlcPath());
        clientConfig.setEnableCheckHash(clientExtension.isEnableCheckHash());
        clientConfig.setUseJNI(clientExtension.isUseJNI());
        clientConfig.setVerbose(clientExtension.isVerbose());

        IosSigningConfiguration iosConfiguration = new IosSigningConfiguration();
        iosConfiguration.setProvidedSigningIdentity(clientExtension.getIosExtension().getSigningIdentity());
        iosConfiguration.setProvidedProvisioningProfile(clientExtension.getIosExtension().getProvisioningProfile());
        iosConfiguration.setSimulatorDevice(clientExtension.getIosExtension().getSimulatorDevice());
        // TODO: Allow frameworks
//        iosConfiguration.setFrameworks(clientExtension.getIosExtension().getFrameworks());
//        iosConfiguration.setFrameworksPaths(clientExtension.getIosExtension().getFrameworksPaths());
        clientConfig.setIosSigningConfiguration(iosConfiguration);
    }

    ProjectConfiguration getClientConfig() {
        return clientConfig;
    }

    void build() {
        configClient();

        if (!getGraalHome().isPresent()) {
            throw new GradleException("GraalVM installation directory not found." +
                    " Either set GRAALVM_HOME as an environment variable or" +
                    " set graalvmHome in the client-plugin configuration");
        }

        try {
            String mainClassName = clientConfig.getMainClassName();
            String name = clientConfig.getAppName();
            for (org.gradle.api.artifacts.Configuration configuration : project.getBuildscript().getConfigurations()) {
                project.getLogger().debug("Configuration = " + configuration);
                DependencySet deps = configuration.getAllDependencies();
                project.getLogger().debug("Dependencies = " + deps);
                deps.forEach(dep -> project.getLogger().debug("Dependency = " + dep));
            }
            project.getLogger().debug("mainClassName = " + mainClassName + " and app name = " + name);
            JavaCompile compileTask = (JavaCompile) project.getTasks().findByName(JavaPlugin.COMPILE_JAVA_TASK_NAME);
            FileCollection classpath = compileTask.getClasspath();
            project.getLogger().debug("Compile classPath = " + classpath.getFiles());
            project.getLogger().debug("Java Class Path = " + System.getProperty("java.class.path"));

            List<Path> classPath = getClassPathFromSourceSets();
            project.getLogger().debug("Runtime classPath = " + classPath);

            String cp0 = classPath.stream()
                    .map(Path::toString)
                    .collect(Collectors.joining(File.pathSeparator));

            Path buildRootPath = project.getLayout().getBuildDirectory().dir("client").get().getAsFile().toPath();
            project.getLogger().debug("BuildRoot: " + buildRootPath);

            String cp = cp0 + File.pathSeparator;
            project.getLogger().debug("CP: " + cp);

            SubstrateDispatcher.nativeCompile(buildRootPath, clientConfig, cp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Path> getClassPathFromSourceSets() {
        List<Path> classPath = Collections.emptyList();
        SourceSetContainer sourceSetContainer = (SourceSetContainer) project.getProperties().get("sourceSets");
        SourceSet mainSourceSet = sourceSetContainer.findByName("main");
        if (mainSourceSet != null) {
            classPath = mainSourceSet.getRuntimeClasspath().getFiles().stream()
                    .filter(File::exists)
                    .map(File::toPath).collect(Collectors.toList());
        }
        return classPath;
    }

    private Optional<String> getGraalHome() {
        String graalvmHome = clientExtension.getGraalvmHome();
        if (graalvmHome != null) {
            return Optional.of(graalvmHome);
        } else if (System.getenv("GRAALVM_HOME") != null) {
            return Optional.of(System.getenv("GRAALVM_HOME"));
        }
        return Optional.empty();
    }

}
