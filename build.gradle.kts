group = "net.oceanias"
version = "2.0.6"

plugins {
    id("java-library")
    id("maven-publish")

    alias(libs.plugins.shadow)

    // TODO: Use this plugin for runtime library downloads and relocations after publication is finished.
//    alias(libs.plugins.zapper)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()

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

    api(libs.commandapi)
    api(libs.configlib)
    api(libs.commons.text)
    api(libs.minitext)

//    zap(libs.commandapi)
//    zap(libs.invui)
//    zap(libs.configlib)
//    zap(libs.commons.text)
//    zap(libs.minitext)

    compileOnly(libs.lombok)
    compileOnly(libs.paper)
    compileOnly(variantOf(libs.inventoryaccess) { classifier("remapped-mojang") })

    compileOnlyApi(libs.invui)
}

//zapper {
//    libsFolder = "libraries"
//    relocationPrefix = "$group.$name.libraries"
//
//    repositories {
//        includeProjectRepositories()
//    }
//
//    relocate("com.bruhdows", "bruhdows")
//    relocate("de.exlll", "exlll")
//    relocate("dev.jorel", "jorel")
//    relocate("net.kyori", "kyori")
//    relocate("org.apache", "apache")
//    relocate("org.intellij", "intellij")
//    relocate("org.jetbrains", "jetbrains")
//    relocate("org.snakeyaml", "snakeyaml")
//    relocate("xyz.xenondevs", "xenondevs")
//}

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