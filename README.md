# GluonFX plugin for Gradle

[![Plugin Portal](https://img.shields.io/maven-metadata/v?label=Gradle%20Plugin%20Portal&metadataUrl=https://plugins.gradle.org/m2/com/gluonhq/gluonfx-gradle-plugin/maven-metadata.xml)](https://plugins.gradle.org/plugin/com.gluonhq.gluonfx-gradle-plugin)
[![Build](https://github.com/gluonhq/gluonfx-gradle-plugin/actions/workflows/build.yml/badge.svg)](https://github.com/gluonhq/gluonfx-gradle-plugin/actions/workflows/build.yml)
[![BSD-3 license](https://img.shields.io/badge/license-BSD--3-%230778B9.svg)](https://opensource.org/licenses/BSD-3-Clause)

GluonFX plugin for gradle projects leverages GraalVM, OpenJDK and JavaFX 11+, 
by compiling into native code the Java Client application and all its required dependencies, 
so it can directly be executed as a native application on the target platform.

# Important Notice

Gluon releases the [GluonFX plugin for Maven](https://github.com/gluonhq/gluonfx-maven-plugin), and this plugin is maintained and kept up to date by the community.

Use at your own risk.

## Getting started

To use the plugin, apply the following steps:

### 1. Apply the plugin

Using the `plugins` DSL, add:

    plugins {
        id 'com.gluonhq.gluonfx-gradle-plugin' version '1.0.3'
    }

This requires adding the plugin repository to the `settings.gradle` file:

    pluginManagement {
        repositories {
            gradlePluginPortal()
        }
    }
    rootProject.name = ...

Alternatively, you can use the `buildscript` DSL:

    buildscript {
        repositories {
            maven {
                url "https://plugins.gradle.org/m2/"
            }
        }
        dependencies {
            classpath 'com.gluonhq:gluonfx-gradle-plugin:1.0.3'
        }
    }
    apply plugin: 'com.gluonhq.gluonfx-gradle-plugin'


### 2. Tasks

You can run the regular tasks to build and run your project as a regular VM project:

    ./gradlew clean build
    ./gradlew run

Once the project is ready, the plugin has these main tasks:    

#### `nativeRunAgent`

This task can be run to use a tracing agent and generate the required config files for native-image.

Run:

    ./gradlew nativeRunAgent

#### `nativeCompile`

This tasks does the AOT compilation. It is a very intensive and lengthy task (several minutes, depending on your project and CPU), 
so it should be called only when the project is ready and runs fine on a VM.

Run:

    ./gradlew build nativeCompile

The results will be available at `$buildDir/client/gvm`.

#### `nativeLink`

When the object is created, this task will generate the native executable for the target platform.

Run:

    ./gradlew nativeLink

The results will be available at `$buildDir/client/$hostPlatform/$AppName`.

#### `nativeBuild`

This task simply combines `nativeCompile` and `nativeLink`.

#### `nativeRun`

Runs the executable in the target platform

Run:

    ./gradlew nativeRun

Or run the three tasks combined:

    ./gradlew build nativeBuild nativeRun

Or run directly the application from command line:

    build/gluonfx/$hostPlatform/$AppName/$AppName    

It will create a distributable native application.

#### `nativePackage`

On mobile only, create a package of the executable in the target platform

Run:

    ./gradlew nativePackage

On iOS, this can be used to create an IPA, on Android it will create an APK.

#### `nativeInstall`

Installs the generated package or the binary.

Run:

    ./gradlew nativeInstall
    
Note: At the moment, this task is only intended for Android and Linux-AArch64.    
    
### Configuration

The plugin allows some configuration to modify the default settings:

```
gluonfx {
    target = "$target"
    attachConfig {
        version = "$version"
        configuration = "implementation";
        services "lifecycle", ...
    }

    bundlesList = []
    resourcesList = []
    reflectionList = []
    jniList = []

    compilerArgs = []
    runtimeArgs = []

    javaStaticSdkVersion = ""
    javafxStaticSdkVersion = ""
    graalvmHome = ""

    verbose = false
    enableSwRendering = false

    remoteHostName = ""
    remoteDir = ""
    
    release {
        // Android
        appLabel = ""
        versionCode = "1"
        versionName = "1.0"
        providedKeyStorePath = ""
        providedKeyStorePassword = ""
        providedKeyAlias = ""
        providedKeyAliasPassword = ""
        // iOS
        bundleName = ""
        bundleVersion = ""
        bundleShortVersion = ""
        providedSigningIdentity = ""
        providedProvisioningProfile = ""
        skipSigning = false
    }
}
```

Check the [maven counterpart section](https://docs.gluonhq.com/#_configuration) for more details.

### Requirements

Check the requirements for the [target platform](https://docs.gluonhq.com/#_platforms) before you get started.

## Issues and Contributions ##

Issues can be reported to the [Issue tracker](https://github.com/gluonhq/gluonfx-gradle-plugin/issues)

Contributions can be submitted via [Pull requests](https://github.com/gluonhq/gluonfx-gradle-plugin/pulls), 
providing you have signed the [Gluon Individual Contributor License Agreement (CLA)](https://cla.gluonhq.com).
