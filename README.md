ImagePicker
========

This is an android library for selecting picture in your phone   

Installation
--------

In `settings.gradle` file, add JitPack maven like below:
```
dependencyResolutionManagement {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the following dependency in app build.gradle:
```
dependencies {
    implementation 'com.github.creativeduck:SimpleDuckGallery:1.0.0'   
}
```

You have to migrate your project to support AndroidX by add following lines on gradle.properties file:
```
android.useAndroidX=true
android.enableJetifier=true
```
Usage
--------

License
--------
   
```
[ColorPicker]   
Copyright 2021 creativeduck
   
Licensed under the Apache License, Version 2.0 (the "License");   
you may not use this file except in compliance with the License.   
You may obtain a copy of the License at   
   
   http://www.apache.org/licenses/LICENSE-2.0   
   
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
