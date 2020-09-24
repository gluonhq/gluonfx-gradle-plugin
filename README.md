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
        id 'com.gluonhq.client-gradle-plugin' version '0.1.32'
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
            classpath 'com.gluonhq:client-gradle-plugin:0.1.32'
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

* Download the latest release version of GraalVM: https://github.com/graalvm/graalvm-ce-builds/releases/tag/vm-20.2.0 by choosing `graalvm-ce-java11-darwin-amd64-20.2.0.tar.gz` from the list of assets and unpack it to a preferred location on your system (e.g. in `/opt`)

* Configure the runtime environment. Set `GRAALVM_HOME` environment variable to the GraalVM installation directory.

  For example:

      export GRAALVM_HOME=/opt/graalvm-ce-java11-20.2.0/Contents/Home

* Set `JAVA_HOME` to point to the GraalVM installation directory:

      export JAVA_HOME=$GRAALVM_HOME

##### Additional requirements

* iOS can be built only on Mac OS X

* Xcode 11+ is required to build for iOS 13+. Install `Xcode` from the [Mac App Store](https://apps.apple.com/us/app/xcode/id497799835?mt=12) if you haven't already. 

* Install `Homebrew`, if you haven't already. Please refer to https://brew.sh/ for more information.

* Install `libusbmuxd`

  Using `brew`:

      brew install --HEAD libusbmuxd

* Install `libimobiledevice`

  Using `brew`:

      brew install --HEAD libimobiledevice

#### Linux and Android

* Download the latest release version of GraalVM: https://github.com/graalvm/graalvm-ce-builds/releases/tag/vm-20.2.0 by choosing `graalvm-ce-java11-linux-amd64-20.2.0.tar.gz` from the list of assets and unpack it to a preferred location on your system (e.g. in `/opt`)

* Configure the runtime environment. Set `GRAALVM_HOME` environment variable to the GraalVM installation directory.

  For example:

      export GRAALVM_HOME=/opt/graalvm-ce-java11-20.2.0

* Set `JAVA_HOME` to point to the GraalVM installation directory:

      export JAVA_HOME=$GRAALVM_HOME

##### Additional requirements

* Android can be built only on Linux OS

The client plugin will download the Android SDK and install the required packages. 

Alternatively, you can define a custom location to the Android SDK by setting the `ANDROID_SDK` environment variable, making sure that you have installed all the packages from the following list:

* platforms;android-28
* platform-tools
* build-tools;29.0.2
* extras;android;m2repository
* extras;google;m2repository
* ndk-bundle (in case you opt to skip this bundle and download Android NDK package separately, set the `ANDROID_NDK` environment variable to its location)

#### Windows

* Download the latest release version of GraalVM: https://github.com/graalvm/graalvm-ce-builds/releases/tag/vm-20.2.0 by choosing `graalvm-ce-java11-windows-amd64-20.2.0.zip` from the list of assets and unzip it to a preferred location on your system.

* Make sure you have installed Visual Studio 2019 with the following components:
  - Choose the English Language Pack
  - C++/CLI support for v142 build tools (14.25 or later)
  - MSVC v142 - VS 2019 C++ x64/x86 build tools (v14.25 or later)
  - Windows Universal CRT SDK
  - Windows 10 SDK (10.0.19041.0 or later)

* Run the maven commands mentioned below in a `x64 Native Tools Command Prompt for VS 2019`. This command prompt can be accessed
from the start menu.

* Configure the runtime environment. Set `GRAALVM_HOME` environment variable to the GraalVM installation directory.

  For example:

      set GRAALVM_HOME=C:\tools\graalvm-ce-java11-20.2.0

* Set `JAVA_HOME` to point to the GraalVM installation directory:

      set JAVA_HOME=%GRAALVM_HOME%

## Issues and Contributions ##

Issues can be reported to the [Issue tracker](https://github.com/gluonhq/client-gradle-plugin/issues)

Contributions can be submitted via [Pull requests](https://github.com/gluonhq/client-gradle-plugin/pulls), 
providing you have signed the [Gluon Individual Contributor License Agreement (CLA)](https://docs.google.com/forms/d/16aoFTmzs8lZTfiyrEm8YgMqMYaGQl0J8wA0VJE2LCCY).
