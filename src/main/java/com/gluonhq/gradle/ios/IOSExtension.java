/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2019, Gluon Software
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
package com.gluonhq.gradle.ios;

import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;

public class IOSExtension {

    private final Project project;

    private List<String> frameworks = new ArrayList<>();
    private List<String> frameworksPaths = new ArrayList<>();

    private String signingIdentity;
    private String provisioningProfile;

    private String simulatorDevice;

    public IOSExtension(Project project) {
        this.project = project;
    }

    public List<String> getFrameworks() {
        return frameworks;
    }

    public void setFrameworks(List<String> frameworks) {
        this.frameworks = frameworks;
    }

    public List<String> getFrameworksPaths() {
        return frameworksPaths;
    }

    public void setFrameworksPaths(List<String> frameworksPaths) {
        this.frameworksPaths = frameworksPaths;
    }

    public String getSigningIdentity() {
        return signingIdentity;
    }

    public void setSigningIdentity(String signingIdentity) {
        this.signingIdentity = signingIdentity;
    }

    public String getProvisioningProfile() {
        return provisioningProfile;
    }

    public void setProvisioningProfile(String provisioningProfile) {
        this.provisioningProfile = provisioningProfile;
    }

    public String getSimulatorDevice() {
        return simulatorDevice;
    }

    public void setSimulatorDevice(String simulatorDevice) {
        this.simulatorDevice = simulatorDevice;
    }
}
