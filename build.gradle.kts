import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.7.20"))
    }
}

group = "com.croquis"
version = "0.0.1-SNAPSHOT"

plugins {
    val kotlinVersion = "1.7.20"
    idea
    jacoco
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion apply false
    kotlin("plugin.spring") version kotlinVersion apply false
    id("org.springframework.boot") version "2.5.3" apply false
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    id("com.avast.gradle.docker-compose") version "0.16.8"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0" apply false
    id("org.jlleitschuh.gradle.ktlint-idea") version "10.1.0"
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    repositories {
        mavenCentral()
    }

    ktlint {
        filter {
            exclude("*.kts")
            exclude("**/generated/**")
        }
    }
}

tasks {
    register<Exec>("lint") {
        commandLine = "./gradlew ktlintCheck".split(" ")
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "jacoco")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    java.sourceCompatibility = JavaVersion.VERSION_11

    val kotestVersion = "4.6.0"

    dependencies {
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("io.kotest:kotest-runner-junit5:${kotestVersion}")
        testImplementation("io.kotest:kotest-assertions-core:${kotestVersion}")
        testImplementation("io.kotest:kotest-property:${kotestVersion}")
        testImplementation("io.kotest:kotest-framework-datatest:${kotestVersion}")
        testImplementation("io.kotest:kotest-extensions-spring:4.4.3")
        testImplementation("io.mockk:mockk:1.12.1")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    jacoco.toolVersion = "0.8.8"

    tasks.test {
        extensions.configure(JacocoTaskExtension::class) {
            val destinationFile = file("$buildDir/jacoco/jacoco.exec")
            setDestinationFile(destinationFile)
        }
        finalizedBy("jacocoTestReport")
    }

    tasks.jacocoTestReport {
        reports {
            html.required.set(true)
            xml.required.set(true)
            xml.outputLocation.set(file("$buildDir/jacoco/jacoco.xml"))
            finalizedBy("jacocoTestCoverageVerification")
        }
    }

    tasks.jacocoTestCoverageVerification {
        violationRules {
            rule {
                element = "CLASS"

                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                }
            }
        }
    }

    tasks.jacocoTestReport {
        reports {
            html.required.set(true)
            xml.required.set(true)
        }
    }
    tasks.test {
        finalizedBy("jacocoTestReport")
    }
}
