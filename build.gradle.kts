plugins {
    id("org.jetbrains.intellij") version "1.15.0"
    kotlin("jvm") version "1.9.10"
}

group = "me.shawaf.visualflow"
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
        sinceBuild.set("241")
        untilBuild.set("241.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
