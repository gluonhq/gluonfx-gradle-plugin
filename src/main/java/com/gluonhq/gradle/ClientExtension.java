/*
 * Copyright (c) 2019, 2022, Gluon
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

import java.util.ArrayList;
import java.util.List;

import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.util.ConfigureUtil;

import com.gluonhq.gradle.attach.AttachConfiguration;

import groovy.lang.Closure;

public class ClientExtension {

    private static final String DEFAULT_TARGET = "host";

    /**
     * Defines the target platform. Default is host, which refers to the platform
     * that currently hosts the process (macosx or linux), but could be also set to
     * ios (either simulator or device), if the host is a Mac.
     * Default is "host"
     */
    private String target;

    /**
     * List of additional full qualified bundle resources that will be added to
     * the default bundles list, that already includes:
     * - "com/sun/javafx/scene/control/skin/resources/controls",
     * - "com.sun.javafx.tk.quantum.QuantumMessagesBundle"
     */
    private final List<String> bundlesList;

    /**
     * List of additional resource patterns or extensions that will be added
     * to the default resource list that already includes:
     * - png, gif, jpg, jpeg, bmp,
     * - ttf, css, fxml, json
     * - frag, gls, license
     */
    private final List<String> resourcesList;

    /**
     * List of additional full qualified classes that will be added to the default
     * reflection list, that already includes most of the JavaFX classes.
     */
    private final List<String> reflectionList;

    /**
     * List of additional full qualified classes that will be added to the default
     * jni list, that already includes most of the JavaFX classes.
     */
    private final List<String> jniList;

    /**
     * List of optional compiler arguments
     */
    private final List<String> compilerArgs;

    /**
     * List of optional linker arguments
     */
    private final List<String> linkerArgs;

    /**
     * List of optional runtime arguments
     */
    private final List<String> runtimeArgs;

    /**
     * The Java static SDK version
     */
    private String javaStaticSdkVersion;

    /**
     * The JavaFX static SDK version
     */
    private String javafxStaticSdkVersion;

    /**
     * The GraalVM Home directory
     */
    private String graalvmHome;

    /**
     * Enables verbose output
     * By default is false
     */
    private boolean verbose;

    /**
     * Enables software rendering.
     * By default is false
     */
    private boolean enableSwRendering;

    /**
     * host name for remote deploying, typically to an
     * embedded system, providing it is reachable and SSH is
     * enabled
     */
    private String remoteHostName;

    /**
     * Sets the directory where the native image will be
     * deployed on the remote system, providing the remote
     * host is reachable and SSH is enabled.
     */
    private String remoteDir;

    /**
     * Sets a unique application identifier.
     */
    private String appIdentifier;

    private AttachConfiguration attachConfiguration;

    private ReleaseConfiguration releaseConfiguration;

    public ClientExtension(Project project, ObjectFactory objectFactory) {
        this.target = DEFAULT_TARGET;
        this.bundlesList = new ArrayList<>();
        this.resourcesList = new ArrayList<>();
        this.reflectionList = new ArrayList<>();
        this.jniList = new ArrayList<>();
        this.compilerArgs = new ArrayList<>();
        this.linkerArgs = new ArrayList<>();
        this.runtimeArgs = new ArrayList<>();

        attachConfiguration = objectFactory.newInstance(AttachConfiguration.class, project);
        releaseConfiguration = objectFactory.newInstance(ReleaseConfiguration.class, project);
    }

    public String getGraalvmHome() {
        return graalvmHome;
    }

    public void setGraalvmHome(String graalvmHome) {
        this.graalvmHome = graalvmHome;
    }

    public String getJavaStaticSdkVersion() {
        return javaStaticSdkVersion;
    }

    public void setJavaStaticSdkVersion(String javaStaticSdkVersion) {
        this.javaStaticSdkVersion = javaStaticSdkVersion;
    }

    public String getJavafxStaticSdkVersion() {
        return javafxStaticSdkVersion;
    }

    public void setJavafxStaticSdkVersion(String javafxStaticSdkVersion) {
        this.javafxStaticSdkVersion = javafxStaticSdkVersion;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<String> getBundlesList() {
        return bundlesList;
    }

    public void setBundlesList(List<String> bundlesList) {
        this.bundlesList.clear();
        this.bundlesList.addAll(bundlesList);
    }

    public List<String> getResourcesList() {
        return resourcesList;
    }

    public void setResourcesList(List<String> resourcesList) {
        this.resourcesList.clear();
        this.resourcesList.addAll(resourcesList);
    }

    public List<String> getReflectionList() {
        return reflectionList;
    }

    public void setReflectionList(List<String> reflectionList) {
        this.reflectionList.clear();
        this.reflectionList.addAll(reflectionList);
    }

    public void setJniList(List<String> jniList) {
        this.jniList.clear();
        this.jniList.addAll(jniList);
    }

    public List<String> getJniList() {
        return jniList;
    }

    public void setCompilerArgs(List<String> compilerArgs) {
        this.compilerArgs.clear();
        this.compilerArgs.addAll(compilerArgs);
    }

    public List<String> getCompilerArgs() {
        return compilerArgs;
    }

    public void setLinkerArgs(List<String> linkerArgs) {
        this.linkerArgs.clear();
        this.linkerArgs.addAll(linkerArgs);
    }

    public List<String> getLinkerArgs() {
        return linkerArgs;
    }

    public void setRuntimeArgs(List<String> compilerArgs) {
        this.runtimeArgs.clear();
        this.runtimeArgs.addAll(compilerArgs);
    }

    public List<String> getRuntimeArgs() {
        return runtimeArgs;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isEnableSwRendering() {
        return enableSwRendering;
    }

    public void setEnableSwRendering(boolean enableSwRendering) {
        this.enableSwRendering = enableSwRendering;
    }

    public void setRemoteHostName(String remoteHostName) {
        this.remoteHostName = remoteHostName;
    }

    public String getRemoteHostName() {
        return remoteHostName;
    }

    public void setRemoteDir(String remoteDir) {
        this.remoteDir = remoteDir;
    }

    public String getRemoteDir() {
        return remoteDir;
    }

    public void setAppIdentifier(String appIdentifier) {
        this.appIdentifier = appIdentifier;
    }

    public String getAppIdentifier() {
        return appIdentifier;
    }

    public void attachConfig(Closure<?> configureClosure) {
        ConfigureUtil.configure(configureClosure, attachConfiguration);
    }

    public AttachConfiguration getAttachConfig() {
        return attachConfiguration;
    }

    public void release(Closure<?> configureClosure) {
        ConfigureUtil.configure(configureClosure, releaseConfiguration);
    }

    public ReleaseConfiguration getReleaseConfiguration() {
        return releaseConfiguration;
    }

}
