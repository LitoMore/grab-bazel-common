# Grab Bazel Common Android

Common rules and macros for Grab's Android projects built with Bazel. This repo provides rules and macros to support some of Android Gradle Plugin features in Bazel.

The repo also hosts a patched Bazel Android Tools jar with fixes for build reproducibility and databinding applied. See [/patches](https://github.com/grab/grab-bazel-common/tree/master/patches) for details.

The rules are used by [Grazel](https://github.com/grab/Grazel) - Gradle plugin to automate migration to Bazel.

# Usage

In `WORKSPACE` file,

```python
load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

git_repository(
    name = "grab_bazel_common",
    commit = "<commit-hash>",
    remote = "https://github.com/grab/grab-bazel-common.git",
)

# Optional patched Android Tools
load("@grab_bazel_common//:workspace_defs.bzl", "android_tools")

android_tools(
    commit = "<commit-hash>",
    remote = "https://github.com/grab/grab-bazel-common.git",
)

# Maven dependencies
load("@grab_bazel_common//:workspace_defs.bzl", "GRAB_BAZEL_COMMON_ARTIFACTS")

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = GRAB_BAZEL_COMMON_ARTIFACTS + [
        # ... 
    ],
    ...
)
```

# Features

### Build Config Fields
`Build Config` support for android projects
```python
 load("@grab_bazel_common//tools/build_config:build_config.bzl", "build_config")

 build_config(
     name = "feature-toggle-build-config",
     package_name = "com.grab.featuretoggle",
     strings = {
         "ID": "Hello",
     },
     ints = {},
     longs = {},
     strings = {},
 )
```
   
### Res values   
`resValue` strings support for Android Projects

```python
 load("@grab_bazel_common//tools/res_value:res_value.bzl", "res_value")
 # Define resValues
 res_value(
     name = "app-res-value",
     custom_package = "com.grab.playground",
     manifest = "src/main/AndroidManifest.xml",
     strings = {
         "prefix": "app",
         "field": "debug"
     },
 )
 
 # Usage of defined resValues
 android_library(
     deps = [
         ":app-res-value",
         ...
     ],
 )
```   

### Databinding 
Provides a macro which in most cases can be used as a drop-in replacement to `kt_android_library` to enable support for Kotlin code when using databinding. [Details](https://github.com/grab/grab-bazel-common/blob/documentation/tools/databinding/databinding.bzl).

```python
load("@grab_bazel_common//tools/databinding:databinding.bzl", "kt_db_android_library")

kt_db_android_library(
    name = "module",
    srcs = glob([
        "src/main/java/com/grab/module/**/*.kt",
    ]),
    assets = glob([
        "src/main/assets/empty_file.txt",
    ]),
    assets_dir = "src/main/assets",
    custom_package = "com.grab.module",
    manifest = "src/main/AndroidManifest.xml",
    resource_files = glob([
        "src/main/res/**",
    ]),
    visibility = [
        "//visibility:public",
    ],
    deps = [
        "@maven//:io_reactivex_rxjava2_rxjava",
    ],
)
```

This requires the following flags in `.bazelrc` file.

```python
# Databinding flags
build --experimental_android_databinding_v2
build --android_databinding_use_v3_4_args
build --android_databinding_use_androidx

# Flags to enable latest android providers in rules
build --experimental_google_legacy_api
query --experimental_google_legacy_api
```


### Custom Resource Sets
Bazel expects certain Android resource folder structure (should start with `res/`) and this can conflict with Android Gradle plugin's custom resource source set feature which does not have this validation. This macro helps to adapt the folder to Bazel expected structure so both build systems can function.

In Gradle, if you have:

```groovy
sourceSets {
    debug {
        res.srcDirs += "src/main/res-debug"
    }
}
```

the Bazel equivalent would be:

```python
load("@grab_bazel_common//tools/custom_res:custom_res.bzl", "custom_res")

android_binary(
    name = "app",
    custom_package = "com.grab.playground",
    manifest = "src/main/AndroidManifest.xml",
    resource_files = glob([
        "src/main/res/**",
    ]) + custom_res( # Wrap the folder with custom_res macro
        dir_name = "res-debug",
        resource_files = glob([
            "src/main/res-debug/**",
        ]),
        target = "app",
    ),
)
```
   
# License

```
Copyright 2021 Grabtaxi Holdings PTE LTE (GRAB)

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