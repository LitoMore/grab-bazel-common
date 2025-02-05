/*
 * Copyright 2021 Grabtaxi Holdings PTE LTE (GRAB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grab.databinding.stub.common

import java.io.File
import java.nio.file.Paths

interface Generator {
    val preferredDir: File?

    val defaultDirName: String

    val outputDir: File
        get() = when {
            preferredDir != null -> File(preferredDir, defaultDirName)
            else -> File(defaultDirName)
        }

    fun logFile(packageName: String, typeName: String) {
        val fileName = Paths.get(
            outputDir.path,
            packageName.replace(".", "/"),
            "$typeName.java"
        )
        println("Generated $fileName")
    }
}