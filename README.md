# Gluon Client plugin for Gradle

The Gluon Client plugin for gradle projects leverages GraalVM, OpenJDK and JavaFX 11+, 
by compiling into native code the Java Client application and all its required dependencies, 
so it can directly be executed as a native application on the target platform.

[![Travis CI](https://api.travis-ci.org/gluonhq/client-gradle-plugin.svg?branch=master)](https://travis-ci.org/gluonhq/client-gradle-plugin)
[![BSD-3 license](https://img.shields.io/badge/license-BSD--3-%230778B9.svg)](https://opensource.org/licenses/BSD-3-Clause)


## Getting started

To use the plugin, apply the following steps:

### 1. Apply the plugin

Using the `plugins` DSL, add:


    plugins {
        id 'com.gluonhq.client-gradle-plugin' version '0.0.11'
    }
    
This requires adding the plugin repository to the `settings.gradle` file:

    pluginManagement {
        repositories {
            maven {
                url "https://nexus.gluonhq.com/nexus/content/repositories/releases"
            }
            gradlePluginPortal()
        }
    }
    rootProject.name = ...

Alternatively, you can use the `buildscript` DSL:

    buildscript {
        repositories {
            maven {
                url "https://nexus.gluonhq.com/nexus/content/repositories/releases"
            }
            maven {
                url "https://plugins.gradle.org/m2/"
            }
        }
        dependencies {
            classpath 'com.gluonhq:client-gradle-plugin:0.0.11'
        }
    }
    apply plugin: 'com.gluonhq.client-gradle-plugin'

### 2. Tasks

You can run the regular tasks to build and run your project as a regular VM project:

    ./gradlew clean build
    ./gradlew run
    
Once the project is ready, the plugin has these three main tasks:    

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

    build/client/$hostPlatform/$AppName/$AppName    
    
It will create a distributable native application.

### 3. Specify plugin extensions

Only if you need to, you can modify the default settings, by specifying
 the required extensions that the project uses in your `build.gradle` file:

    gluonClient {
        target = "host"
        graalLibsVersion = "20.0.0-ea+12"
        graalLibsPath = ""
        javaStaticSdkVersion = "11-ea+6"
        javafxStaticSdkVersion = "13-ea+7"
        bundlesList = []
        resourcesList = []
        reflectionList = []
        jniList = []
        delayInitList = []
        releaseSymbolsList = []
        verbose = false
        attachConfig {
            version = "4.0.2"
            services ''
            configuration = "implementation"
        }
    }
    
### Requirements

At this moment the plugin is in beta, and supports Linux, Mac OS X and iOS platforms for now.

To use the plugin to develop and deploy native applications on Mac or iOS platforms, you need a Mac with MacOS X 10.13.2 or superior, and Xcode 9.2 or superior, available from the Mac App Store. Once Xcode is downloaded and installed, open it and accept the license terms.

For now, only JDK 11 is supported. Any JDK 11 distribution that doesn't bundle JavaFX is valid, like:

- [OpenJDK 11.0.2](https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_osx-x64_bin.tar.gz)

- [AdoptOpenJDK 11.0.3](https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.3%2B7/OpenJDK11U-jdk_x64_mac_hotspot_11.0.3_7.tar.gz)

Once downloaded and installed, set `JAVA_HOME` pointing to that JDK 11.

Check the [documentation](https://docs.gluonhq.com/client) for more details about the plugin and running the [gradle samples](https://github.com/gluonhq/client-samples/tree/master/Gradle).

