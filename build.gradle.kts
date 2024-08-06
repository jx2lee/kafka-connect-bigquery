/*
 * Copyright 2024-present Coinone, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.io.ByteArrayOutputStream

description = "Coinone BigQuery Sink Connector"

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    java
    id("com.github.gmazzo.buildconfig") version "5.4.0"
    id("com.github.johnrengelman.shadow") version "8.1.0"

}

group = "com.coinone"
version = "0.0.1"

val connectApiVersion by extra("3.6.1")
val defaultJdkVersion = 17

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(defaultJdkVersion))
    }
}

// Generated files
val gitVersion: String by lazy {
    val describeStdOut = ByteArrayOutputStream()
    exec {
        commandLine = listOf("git", "describe", "--tags", "--always", "--dirty")
        standardOutput = describeStdOut
    }
    describeStdOut.toString().substring(1).trim()
}

buildConfig {
    className("Versions")
    packageName("com.coinone.kafka.connect")
    useJavaOutput()
    buildConfigField("String", "NAME", "\"bigquery-kafka\"")
    buildConfigField("String", "VERSION", provider { "\"${version}\"" })
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

val bigqueryDependencies: Configuration by configurations.creating

dependencies {
    implementation("org.apache.kafka:connect-api:$connectApiVersion")
    implementation("org.slf4j:slf4j-simple:2.0.11")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

// ShadowJar
tasks.withType<ShadowJar> {
    archiveAppendix.set("connect")
    doLast {
        val fatJar = archiveFile.get().asFile
        val fatJarSize = "%.4f".format(fatJar.length().toDouble() / (1_000 * 1_000))
        println("FatJar: ${fatJar.path} ($fatJarSize MB)")
    }

    tasks.named("shadowJar").configure {
        enabled = false
    }
}

tasks.test {
    useJUnitPlatform()
}
