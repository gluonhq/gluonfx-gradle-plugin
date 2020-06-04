/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2020, Gluon Software
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

import org.gradle.api.Project;

import javax.inject.Inject;

public class ReleaseConfiguration {

    public static final String DEFAULT_BUNDLE_VERSION = "1.0";
    public static final String DEFAULT_BUNDLE_SHORT_VERSION = "1.0";

    public static final String DEFAULT_CODE_VERSION = "1";
    public static final String DEFAULT_CODE_NAME = "1.0";
    public static final String DEFAULT_DEBUG_KEY_STORE_PASSWORD = "android";
    public static final String DEFAULT_DEBUG_KEY_ALIAS = "androiddebugkey";
    public static final String DEFAULT_DEBUG_KEY_ALIAS_PASSWORD = "android";

    private final Project project;

    // iOS

    /**
     * A user-visible short name for the bundle
     *
     * Default: if not set, $appName will be used.
     */
    private String bundleName;

    /**
     * The version of the build that identifies an iteration of the bundle. A
     * string composed of one to three period-separated integers, containing
     * numeric characters (0-9) and periods only.
     *
     * Default: 1.0
     */
    private String bundleVersion;

    /**
     * A user-visible string for the release or version number of the bundle. A
     * string composed of one to three period-separated integers, containing
     * numeric characters (0-9) and periods only.
     *
     * Default: 1.0
     */
    private String bundleShortVersion;

    /**
     * String that identifies a valid certificate that will be used for iOS development
     * or iOS distribution.
     *
     * Default: null. When not provided, Substrate will be selected from all the valid identities found
     * installed on the machine from any of these types:
     *
     *      iPhone Developer|Apple Development|iOS Development|iPhone Distribution
     *
     * and that were used by the provisioning profile.
     */
    private String providedSigningIdentity;

    /**
     * String with the name of the provisioning profile created for iOS development or
     * distribution of the given app.
     *
     * Default: null. When not provided, Substrate will try to find a valid installed
     * provisioning profile that can be used to sign the app, including wildcards.
     */
    private String providedProvisioningProfile;

    /**
     * Boolean that can be used to skip signing iOS apps. This will prevent any
     * deployment, but can be useful to run tests without an actual device
     */
    private boolean skipSigning;

    /**
     * A string with a valid name of an iOS simulator device
     */
    private String simulatorDevice;

    // Android

    /**
     * A user-visible short name for the app
     *
     * Default: if not set, $appName will be used.
     */
    private String appLabel;

    /**
     * A positive integer used as an internal version number
     *
     * Default: 1
     */
    private String versionCode;

    /**
     * A string used as the version number shown to users, like
     * <major>.<minor>.<point>
     *
     * Default: 1.0
     */
    private String versionName;

    /**
     * A string with the path to a keystore file that can be used to sign
     * the Android apk.
     *
     * Default: null. If not set, Substrate creates and uses a debug keystore.
     */
    private String providedKeyStorePath;

    /**
     * A string with the password of the provide keystore file.
     *
     * Default: null. If not set, Substrate creates and uses a debug keystore.
     */
    private String providedKeyStorePassword;

    /**
     * A string with an identifying name for the key
     *
     * Default: null. If not set, Substrate creates and uses a debug keystore.
     */
    private String providedKeyAlias;

    /**
     * A string with a password for the key
     *
     * Default: null. If not set, Substrate creates and uses a debug keystore.
     */
    private String providedKeyAliasPassword;

    @Inject
    public ReleaseConfiguration(Project project) {
        this.project = project;
    }

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    public String getBundleVersion() {
        return bundleVersion;
    }

    public void setBundleVersion(String bundleVersion) {
        this.bundleVersion = bundleVersion;
    }

    public String getBundleShortVersion() {
        return bundleShortVersion;
    }

    public void setBundleShortVersion(String bundleShortVersion) {
        this.bundleShortVersion = bundleShortVersion;
    }

    public String getProvidedSigningIdentity() {
        return providedSigningIdentity;
    }

    public void setProvidedSigningIdentity(String providedSigningIdentity) {
        this.providedSigningIdentity = providedSigningIdentity;
    }

    public String getProvidedProvisioningProfile() {
        return providedProvisioningProfile;
    }

    public void setProvidedProvisioningProfile(String providedProvisioningProfile) {
        this.providedProvisioningProfile = providedProvisioningProfile;
    }

    public boolean isSkipSigning() {
        return skipSigning;
    }

    public void setSkipSigning(boolean skipSigning) {
        this.skipSigning = skipSigning;
    }

    public String getSimulatorDevice() {
        return simulatorDevice;
    }

    public void setSimulatorDevice(String simulatorDevice) {
        this.simulatorDevice = simulatorDevice;
    }

    public String getAppLabel() {
        return appLabel;
    }

    public void setAppLabel(String appLabel) {
        this.appLabel = appLabel;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getProvidedKeyStorePath() {
        return providedKeyStorePath;
    }

    public void setProvidedKeyStorePath(String providedKeyStorePath) {
        this.providedKeyStorePath = providedKeyStorePath;
    }

    public String getProvidedKeyStorePassword() {
        return providedKeyStorePassword;
    }

    public void setProvidedKeyStorePassword(String providedKeyStorePassword) {
        this.providedKeyStorePassword = providedKeyStorePassword;
    }

    public String getProvidedKeyAlias() {
        return providedKeyAlias;
    }

    public void setProvidedKeyAlias(String providedKeyAlias) {
        this.providedKeyAlias = providedKeyAlias;
    }

    public String getProvidedKeyAliasPassword() {
        return providedKeyAliasPassword;
    }

    public void setProvidedKeyAliasPassword(String providedKeyAliasPassword) {
        this.providedKeyAliasPassword = providedKeyAliasPassword;
    }

    @Override
    public String toString() {
        return "ReleaseConfiguration{" +
                "bundleName='" + bundleName + '\'' +
                ", bundleVersion='" + bundleVersion + '\'' +
                ", bundleShortVersion='" + bundleShortVersion + '\'' +
                ", providedSigningIdentity='" + providedSigningIdentity + '\'' +
                ", providedProvisioningProfile='" + providedProvisioningProfile + '\'' +
                ", skipSigning=" + skipSigning +
                ", simulatorDevice='" + simulatorDevice + '\'' +
                ", appLabel='" + appLabel + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", versionName='" + versionName + '\'' +
                ", providedKeyStorePath='" + providedKeyStorePath + '\'' +
                ", providedKeyStorePassword='" + providedKeyStorePassword + '\'' +
                ", providedKeyAlias='" + providedKeyAlias + '\'' +
                ", providedKeyAliasPassword='" + providedKeyAliasPassword + '\'' +
                '}';
    }

    public com.gluonhq.substrate.model.ReleaseConfiguration toSubstrate() {
        com.gluonhq.substrate.model.ReleaseConfiguration release = new com.gluonhq.substrate.model.ReleaseConfiguration();

        release.setBundleName(getBundleName());
        release.setBundleVersion(getBundleVersion());
        release.setBundleShortVersion(getBundleShortVersion());
        release.setProvidedSigningIdentity(getProvidedSigningIdentity());
        release.setProvidedProvisioningProfile(getProvidedProvisioningProfile());
        release.setSkipSigning(isSkipSigning());
        release.setSimulatorDevice(getSimulatorDevice());

        release.setAppLabel(getAppLabel());
        release.setVersionCode(getVersionCode());
        release.setVersionName(getVersionName());
        release.setProvidedKeyStorePath(getProvidedKeyStorePath());
        release.setProvidedKeyStorePassword(getProvidedKeyStorePassword());
        release.setProvidedKeyAlias(getProvidedKeyAlias());
        release.setProvidedKeyAliasPassword(getProvidedKeyAliasPassword());

        return release;
    }
}

