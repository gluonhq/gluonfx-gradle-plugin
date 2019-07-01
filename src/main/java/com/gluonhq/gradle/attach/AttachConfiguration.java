/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2018, 2019, Gluon Software
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

import com.gluonhq.gradle.ClientExtension;
import com.gluonhq.omega.attach.AttachResolver;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AttachConfiguration {

    private Project project;

    private String version = "4.0.2";
    private NamedDomainObjectContainer<AttachServiceDefinition> services;

    @Inject
    public AttachConfiguration(Project project) {
        this.project = project;
        this.services = project.container(AttachServiceDefinition.class);
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

    public Collection<AttachServiceDefinition> getServices() {
        return services;
    }

    public void services(String... services) {
        if (services != null) {
            for (String service : services) {
                this.services.create(service);
            }
            applyConfiguration(project.getConfigurations().getByName("implementation"),
                    project.getExtensions().getByType(ClientExtension.class).getTarget());
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
     *
     * @param configuration the configuration where the service dependencies are added to
     * @param target the target platform
     */
    private void applyConfiguration(Configuration configuration, String target) {
        project.getLogger().info("Adding Attach dependencies for target: " + target);
        if (services != null) {
            services.forEach(serviceDefinition -> project.getDependencies()
                            .add(configuration.getName(), generateDependencyNotation(configuration, serviceDefinition, target)));

            Map<String, String> utilDependencyNotationMap = new HashMap<>();
            utilDependencyNotationMap.put("group", AttachResolver.DEPENDENCY_GROUP);
            utilDependencyNotationMap.put("name", AttachResolver.UTIL_ARTIFACT);
            utilDependencyNotationMap.put("version", getVersion());
            project.getDependencies().add(configuration.getName(), utilDependencyNotationMap);
        }
    }

    private Object generateDependencyNotation(Configuration configuration, AttachServiceDefinition pluginDefinition, String target) {
        Map<String, String> dependencyNotationMap = new HashMap<>();
        dependencyNotationMap.put("group", AttachResolver.DEPENDENCY_GROUP);
        dependencyNotationMap.put("name", getDependencyName(pluginDefinition));
        dependencyNotationMap.put("version", getDependencyVersion(pluginDefinition));
        dependencyNotationMap.put("classifier", getDependencyClassifier(pluginDefinition, target));

        project.getLogger().info("Adding dependency for {} in configuration {}: {}", pluginDefinition.getService().getServiceName(), configuration.getName(), dependencyNotationMap);
        return dependencyNotationMap;
    }

    private String getDependencyName(AttachServiceDefinition pluginDefinition) {
        return pluginDefinition.getName();
    }

    private String getDependencyVersion(AttachServiceDefinition pluginDefinition) {
        return pluginDefinition.getVersion() == null ? version : pluginDefinition.getVersion();
    }

    private String getDependencyClassifier(AttachServiceDefinition pluginDefinition, String target) {
        return pluginDefinition.getSupportedPlatform(target);
    }

}
