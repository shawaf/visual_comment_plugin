import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.intellij") version "1.15.0"
    kotlin("jvm") version "1.9.10"
}

group = "me.shawaf.visualcomment"
version = "1.0.0"

repositories {
    mavenCentral()
}

intellij {
    version.set("2024.1.2") // Match your Android Studio's IntelliJ version
    type.set("IC") // IC = IntelliJ Community, IU = IntelliJ Ultimate
    plugins.set(listOf("java")) // Add additional plugins your code depends on
}

dependencies {
    implementation(kotlin("stdlib"))
}

tasks {
    patchPluginXml {
        version.set(project.version.toString())
        sinceBuild.set("242")
        untilBuild.set("242.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    // Ensure Kotlin and Java targets are the same (JVM 17)
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(17)  // Ensure Java targets JVM 17 (set Java 17 compatibility)
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "17"  // Ensure Kotlin targets JVM 17
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // Use JDK 17 only for this project
    }
}

