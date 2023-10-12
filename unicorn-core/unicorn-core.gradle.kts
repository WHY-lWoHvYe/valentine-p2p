/*
 * This file was generated by the Gradle 'init' task.
 */
import org.springframework.boot.gradle.plugin.SpringBootPlugin

//configurations {
//    compileOnly {
//        extendsFrom annotationProcessor
//    }
//}

description = "系统核心模块"

java {
    withJavadocJar()
    // 这个要放到dependencies之前
    registerFeature("log4jdbc") {
        usingSourceSet(sourceSets["main"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.javaModuleVersion = provider { project.version as String }
}

val sharedManifest = java.manifest {
    attributes(
        "Developer" to "lWoHvYe",
        "Created-By" to "Gradle",
        "Built-By" to System.getProperty("user.name"),
        "Build-Jdk-Spec" to System.getProperty("java.version"),
    )
}

tasks.jar {
    enabled = true
    manifest {
        from(sharedManifest)
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Automatic-Module-Name" to "lwohvye.${project.name.replace("-", ".")}"
        )
    }
    into("META-INF/maven/${project.group}/${project.name}") {
        from("generatePomFileForMavenJavaCorePublication")
        rename(".*", "pom.xml")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJavaCore") {
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Unicorn Core")
                description.set("Core module with BaseConfig, Utils, QueryAnno and so on")
                url.set("https://github.com/lWoHvYe/unicorn.git")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("lWoHvYe")
                        name.set("王红岩(lWoHvYe)")
                        email.set("lWoHvYe@outlook.com")
                        url.set("https://www.lwohvye.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/lWoHvYe/unicorn.git")
                    developerConnection.set("scm:git:ssh://github.com/lWoHvYe/unicorn.git")
                    url.set("https://github.com/lWoHvYe/unicorn/tree/main")
                }
            }
        }
    }
}

dependencies {
    api(platform(SpringBootPlugin.BOM_COORDINATES))
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-jose")
    api("org.springframework.boot:spring-boot-starter-amqp")
    api("org.springframework.boot:spring-boot-starter-cache")
    api("org.springframework.boot:spring-boot-starter-data-redis")
    api(libs.redisson)
    api("org.apache.commons:commons-pool2")
    api("org.apache.commons:commons-lang3")
    api(libs.springdoc.webmvc.ui)
    api(libs.hutool)
    api(libs.ip2region)
    api(libs.poi)
    api(libs.poi.ooxml)
    implementation(libs.xerces)
    api(libs.mapstruct)
//    mapstruct-spring-extensions seems unused
    api(libs.mapstruct.spring.annotations)
    api("org.hibernate.validator:hibernate-validator")
    api("com.github.ben-manes.caffeine:caffeine")
    implementation(libs.logback.encoder)
    api("org.bouncycastle:bcpkix-jdk18on:1.72")
    api(libs.thumbnailator)
    api("org.jetbrains:annotations:24.0.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    runtimeOnly("com.mysql:mysql-connector-j")
    "log4jdbcRuntimeOnly"(libs.log4jdbc)
}

