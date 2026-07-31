/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2018, 2023, Gluon Software
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
package com.gluonhq.gradle.attach;

import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

import com.gluonhq.gradle.ClientExtension;
import com.gluonhq.substrate.Constants;
import org.gradle.api.artifacts.ExternalModuleDependency;
import org.gradle.api.artifacts.ModuleDependency;

public class AttachConfiguration {
    private static final String DEPENDENCY_GROUP = "com.gluonhq.attach";
    private static final String UTIL_ARTIFACT = "util";

    private Project project;

    private String version;
    private String configuration = "implementation";

    private NamedDomainObjectContainer<AttachServiceDefinition> services;
    private Configuration lastAppliedConfiguration;

    @Inject
    public AttachConfiguration(Project project) {
        this.project = project;
        this.services = project.getObjects().domainObjectContainer(AttachServiceDefinition.class);
    }

    public void version(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
        applyConfiguration();
    }

    public String getConfiguration() {
        return configuration;
    }
    public Collection<AttachServiceDefinition> getServices() {
        return services;
    }

    public void services(String... services) {
        if (services != null) {
            for (String service : services) {
                this.services.create(service);
            }
            applyConfiguration();
        }
    }

    /**
     * Configures attach services.
     * @param action action parameter
     */
    public void services(Action<? super NamedDomainObjectContainer<AttachServiceDefinition>> action) {
        action.execute(services);
    }

    /**
     * Add dependencies to the specified configuration. Only dependencies to services that support the provided
     * configuration will be included.
     */
    private void applyConfiguration() {
        if (version == null) {
            throw new IllegalStateException("Attach version must be specified!");
        }

        if (lastAppliedConfiguration != null) {
            lastAppliedConfiguration.getDependencies()
                    .removeIf(dependency -> DEPENDENCY_GROUP.equals(dependency.getGroup()));
        }

        String configName = getConfiguration();
        Configuration configuration = project.getConfigurations().getByName(configName);
        String target = project.getExtensions().getByType(ClientExtension.class).getTarget();

        project.getLogger().info("Adding Attach dependencies for target: " + target);
        if (services != null && !services.isEmpty()) {
            services.forEach(asd -> {
                ModuleDependency dep = generateDependency(asd, target, configName);
                if (dep != null) {
                    dep.exclude(Map.of("group", "org.openjfx", "module", "*"));
                }
            });

            // Also add util artifact if any other artifact added
            String utilClassifier = null;
            if (Constants.PROFILE_ANDROID.equals(target) || Constants.PROFILE_IOS.equals(target)
                    || Constants.PROFILE_IOS_SIM.equals(target)) {
                utilClassifier = Constants.PROFILE_IOS_SIM.equals(target) ?
                        Constants.PROFILE_IOS : target;
            }
            ModuleDependency dep = createDependency(UTIL_ARTIFACT, utilClassifier, configName);
            if (dep != null) {
                dep.exclude(Map.of("group", "org.openjfx", "module", "*"));
            }
        }

        lastAppliedConfiguration = configuration;
    }

    private ModuleDependency generateDependency(AttachServiceDefinition asd, String target, String configName) {
        ModuleDependency dep = createDependency(asd.getName(), asd.getSupportedPlatform(target), configName);

        project.getLogger().info("Adding dependency for {} in configuration {}: {}:{}:{}:{}", asd.getService().getServiceName(), getConfiguration(),
                DEPENDENCY_GROUP, asd.getName(), getVersion(), asd.getSupportedPlatform(target));

        return dep;
    }

    /**
     * Creates and adds a dependency on "group:name:version" (Gradle's non-deprecated single-string
     * notation) and, when a classifier is given, attaches it via the equally non-deprecated
     * {@link ModuleDependency#artifact(Action)} API rather than embedding it in the coordinate
     * string — this mirrors exactly what the old Map-based ("classifier" key) notation did
     * internally, since embedding the classifier in the coordinate string altered dependency
     * resolution in a way that broke native-image compilation for consumers.
     */
    private ModuleDependency createDependency(String name, String classifier, String configName) {
        ExternalModuleDependency dep = (ExternalModuleDependency) project.getDependencies()
                .create(DEPENDENCY_GROUP + ":" + name + ":" + getVersion());
        if (classifier != null) {
            dep.artifact(artifact -> {
                artifact.setName(name);
                artifact.setType("jar");
                artifact.setExtension("jar");
                artifact.setClassifier(classifier);
            });
        }
        return (ModuleDependency) project.getDependencies().add(configName, dep);
    }
}
