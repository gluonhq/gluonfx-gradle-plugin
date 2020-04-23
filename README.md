# Gluon Client plugin for Gradle

The Gluon Client plugin for gradle projects leverages GraalVM, OpenJDK and JavaFX 11+, 
by compiling into native code the Java Client application and all its required dependencies, 
so it can directly be executed as a native application on the target platform.

[![Travis CI](https://api.travis-ci.org/gluonhq/client-gradle-plugin.svg?branch=master)](https://travis-ci.org/gluonhq/client-gradle-plugin)
[![BSD-3 license](https://img.shields.io/badge/license-BSD--3-%230778B9.svg)](https://opensource.org/licenses/BSD-3-Clause)

# Important Notice

Gluon releases the [Client plugin for Maven](https://github.com/gluonhq/client-maven-plugin), and this plugin is maintained and kept up to date by the community.

Use at your own risk.

## Getting started

To use the plugin, apply the following steps:

### 1. Apply the plugin

Using the `plugins` DSL, add:


    plugins {
        id 'com.gluonhq.client-gradle-plugin' version '0.1.20'
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
            classpath 'com.gluonhq:client-gradle-plugin:0.1.20'
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

#### `nativePackage`

On mobile only, create a package of the executable in the target platform

Run:

	./gradlew nativePackage

On iOS, this can be used to create an IPA, on Android it will create an APK.


#### `nativeInstall`

On mobile only, installs the generated package that was created after `nativePackage`.

Run:

	./gradlew nativeInstall
    
### Requirements

#### Mac OS X and iOS

* Download this version of Graal VM: https://download2.gluonhq.com/substrate/graalvm/graalvm-svm-darwin-20.1.0-ea+27.zip and unpack it like you would any other JDK. (e.g. in `/opt`)

* Configure the runtime environment. Set `GRAALVM_HOME` environment variable to the GraalVM installation directory.

For example:

    export GRAALVM_HOME=/opt/graalvm-svm-darwin-20.1.0-ea+27

* Set `JAVA_HOME` to point to the GraalVM installation directory

For example:

    export JAVA_HOME=$GRAALVM_HOME

By default `target` is set to `host`. To deploy to iOS, set the target:

```
gluonClient {
     target = "ios"
 }
```

#### Linux and Android

* Download this version of Graal VM: https://download2.gluonhq.com/substrate/graalvm/graalvm-svm-linux-20.1.0-ea+27.zip and unpack it like you would any other JDK. (e.g. in `/opt`)

* Configure the runtime environment. Set `GRAALVM_HOME` environment variable to the GraalVM installation directory.

For example:

    export GRAALVM_HOME=/opt/graalvm-svm-linux-20.1.0-ea+27

* Set `JAVA_HOME` to point to the GraalVM installation directory

For example:

    export JAVA_HOME=$GRAALVM_HOME

By default `target` is set to `host`. To deploy to Android, set the target:

```
gluonClient {
     target = "android"
 }
```

Check the [documentation](https://docs.gluonhq.com/client) for more details about the plugin and running the [gradle samples](https://github.com/gluonhq/client-samples/tree/master/Gradle).

## Issues and Contributions ##

Issues can be reported to the [Issue tracker](https://github.com/gluonhq/client-samples/issues)

Contributions can be submitted via [Pull requests](https://github.com/gluonhq/client-samples/pulls), 
providing you have signed the [Gluon Individual Contributor License Agreement (CLA)](https://docs.google.com/forms/d/16aoFTmzs8lZTfiyrEm8YgMqMYaGQl0J8wA0VJE2LCCY) 
(See [What is a CLA and why do I care](https://www.clahub.com/pages/why_cla) in case of doubt).
