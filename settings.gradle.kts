/*
 *    Copyright (c) 2024.  lWoHvYe(Hongyan Wang)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/*
 * This file was generated by the Gradle 'init' task.
 */
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("com.gradle.enterprise") version "3.15"
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.8.0")
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        gradlePluginPortal()
        flatDir {
            dirs(rootProject.projectDir.resolve("ex-lib"))
        }
    }

    versionCatalogs {
        create("libs") {
            version("asm", "9.6")
            version("bizlog", "3.0.6")
            version("captcha", "2.1.0")
            version("commonsBeanutils", "1.9.4")
            version("commonsConfiguration", "2.9.0")
            version("easyCaptcha", "1.6.2")
            version("hutool", "5.8.20")
            version("ip2region", "3.0.4")
            version("jjwt", "0.12.5")
            version("kotlin", providers.gradleProperty("kotlinVersion").get())
            version("kotlinxCoroutines", "1.8.0")
            version("logstash", "7.4")
            version("mapstruct", "1.5.5.Final")
            version("mapstructSpring", "1.1.1")
            version("oshiCore", "6.4.2")
            version("poi", "5.2.4")
            version("quartz", "2.3.2")
            version("redisson", "3.27.2")
            version("springBoot", providers.gradleProperty("springBootVersion").get())
            version("springdoc", "2.4.0")
            version("thumbnailator", "0.4.20")
            version("xerces", "2.12.2")

            plugin("spring-boot", "org.springframework.boot").versionRef("springBoot")
            plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm").versionRef("kotlin")
            plugin("kotlin-spring", "org.jetbrains.kotlin.plugin.spring").versionRef("kotlin")
            plugin("kotlin-jpa", "org.jetbrains.kotlin.plugin.jpa").versionRef("kotlin")
            plugin("kotlin-lombok", "org.jetbrains.kotlin.plugin.lombok").versionRef("kotlin")

            library("asm", "org.ow2.asm", "asm").versionRef("asm")
            library("bizlog", "io.github.mouzt", "bizlog-sdk").versionRef("bizlog")
            library("captcha", "com.lwohvye.captcha", "captcha-spring-boot-starter").versionRef("captcha")
            library("commons-beanutils", "commons-beanutils", "commons-beanutils").versionRef("commonsBeanutils")
            library("commons-configuration", "org.apache.commons", "commons-configuration2")
                .versionRef("commonsConfiguration")
            library("coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core")
                .versionRef("kotlinxCoroutines")
            library("coroutines-jdk8", "org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8")
                .versionRef("kotlinxCoroutines")
            library("coroutines-jdk9", "org.jetbrains.kotlinx", "kotlinx-coroutines-jdk9")
                .versionRef("kotlinxCoroutines")
            library("easy-captcha", "com.github.whvcse", "easy-captcha").versionRef("easyCaptcha")
            library("hutool", "cn.hutool", "hutool-all").versionRef("hutool")
            library("ip2region", "net.dreamlu", "mica-ip2region").versionRef("ip2region")
            library("jjwt", "io.jsonwebtoken", "jjwt").versionRef("jjwt")
            library("logback-encoder", "net.logstash.logback", "logstash-logback-encoder").versionRef("logstash")
            library("mapstruct", "org.mapstruct", "mapstruct").versionRef("mapstruct")
            library("mapstruct-processor", "org.mapstruct", "mapstruct-processor").versionRef("mapstruct")
            library("mapstruct-spring", "org.mapstruct.extensions.spring", "mapstruct-spring-extensions")
                .versionRef("mapstructSpring")
            library("mapstruct-spring-annotations", "org.mapstruct.extensions.spring", "mapstruct-spring-annotations")
                .versionRef("mapstructSpring")
            library("oshi-core", "com.github.oshi", "oshi-core").versionRef("oshiCore")
            library("poi", "org.apache.poi", "poi").versionRef("poi")
            library("poi-ooxml", "org.apache.poi", "poi-ooxml").versionRef("poi")
            library("quartz", "org.quartz-scheduler", "quartz").versionRef("quartz")
            library("redisson", "org.redisson", "redisson-spring-boot-starter").versionRef("redisson")
            library("springdoc-webflux-ui", "org.springdoc", "springdoc-openapi-starter-webflux-ui")
                .versionRef("springdoc")
            library("springdoc-webmvc-ui", "org.springdoc", "springdoc-openapi-starter-webmvc-ui")
                .versionRef("springdoc")
            library("thumbnailator", "net.coobird", "thumbnailator").versionRef("thumbnailator")
            library("xerces", "xerces", "xercesImpl").versionRef("xerces")

            bundle("coroutines", listOf("coroutines-core", "coroutines-jdk8", "coroutines-jdk9"))
        }
    }
}

rootProject.name = "valentine-p2p"

val buildFiles = fileTree(rootDir) {
    val excludes = gradle.startParameter.projectProperties["excludeProjects"]?.split(",")
    include("**/*.gradle", "**/*.gradle.kts")
    exclude(
        "build",
        "**/gradle",
        "settings.gradle",
        "settings.gradle.kts",
        "buildSrc",
        "/build.gradle",
        "/build.gradle.kts",
        ".*",
        "out"
    )
    if (excludes != null) {
        exclude(*excludes.toTypedArray())
    }
}

buildFiles.forEach { buildFile ->
    val isDefaultName = buildFile.name == "build.gradle" || buildFile.name == "build.gradle.kts"
    val isKotlin = buildFile.name.endsWith(".kts")

    if (isDefaultName) {
        val buildFilePath = buildFile.parentFile.absolutePath
        val projectPath = buildFilePath.replace(rootDir.absolutePath, "").replace(File.separator, ":")
        include(projectPath)
    } else {
        val projectName =
            if (isKotlin) {
                buildFile.name.replace(".gradle.kts", "")
            } else {
                buildFile.name.replace(".gradle", "")
            }

        val projectPath = ":$projectName"
        include(projectPath)

        val project = findProject(projectPath)
        project?.name = projectName
        project?.projectDir = buildFile.parentFile
        project?.buildFileName = buildFile.name
    }
}

gradleEnterprise {
    val runsOnCI = providers.environmentVariable("CI").getOrElse("false").toBoolean()
    if (runsOnCI) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}
