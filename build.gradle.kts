group = "net.oceanias"
version = "5.2.1"

plugins {
    id("java-library")
    id("maven-publish")

    alias(libs.plugins.shadow)
    alias(libs.plugins.zapper)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenLocal()
    mavenCentral()

    maven("https://central.sonatype.com/repository/maven-snapshots/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.xenondevs.xyz/releases")
    maven("https://jitpack.io")
}

publishing {
    publications {
        create<MavenPublication>("jitpack") {
            from(components["java"])
        }
    }
}

dependencies {
    annotationProcessor(libs.lombok)

    api(libs.zapper)

    compileOnly(libs.lombok)
    compileOnly(libs.paper)
    compileOnly(variantOf(libs.inventoryaccess) { classifier("remapped-mojang") })

    compileOnlyApi(libs.invui)
    compileOnlyApi(libs.commandapi)
    compileOnlyApi(libs.configlib)
    compileOnlyApi(libs.commons.text)
    compileOnlyApi(libs.minitext)

    zap(libs.commandapi)
    zap(libs.configlib)
    zap(libs.commons.text)
    zap(libs.minitext)
}

zapper {
    relocationPrefix = "$group.${name.lowercase()}.libraries"
}

defaultTasks("build")

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    withType<Jar>().configureEach {
        archiveBaseName.set(rootProject.name.replaceFirstChar { char -> char.uppercase() })
    }

    build {
        dependsOn(jar)
    }
}