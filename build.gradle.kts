project.group = "net.oceanias.ocean"
project.version = "1.0.0"

plugins {
    id("java-library")
    id("maven-publish")

    alias(libs.plugins.shadow)
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
}

publishing {
    publications {
        create<MavenPublication>("jitpack") {
            artifact(tasks.shadowJar)

            artifact(tasks.named<Jar>("sourcesJar"))
            artifact(tasks.named<Jar>("javadocJar"))

            pom {
                withXml {
                    asNode().appendNode("repositories").apply {
                        appendNode("repository").apply {
                            appendNode("id", "codemc")
                            appendNode("url", "https://repo.codemc.io/repository/maven-public/")
                        }
                        appendNode("repository").apply {
                            appendNode("id", "papermc")
                            appendNode("url", "https://repo.papermc.io/repository/maven-public/")
                        }
                        appendNode("repository").apply {
                            appendNode("id", "sonatype")
                            appendNode("url", "https://oss.sonatype.org/content/groups/public/")
                        }
                        appendNode("repository").apply {
                            appendNode("id", "xenondevs")
                            appendNode("url", "https://repo.xenondevs.xyz/releases")
                        }
                    }
                }
            }
        }
    }
}

dependencies {
    annotationProcessor(libs.lombok)

    api(libs.invUI)
    api(libs.commandAPI)

    compileOnly(libs.lombok)
    compileOnly(libs.paper)

    implementation(libs.configLib)
    implementation(libs.commonsText)
}

defaultTasks("build")

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }

    jar {
        enabled = true

        manifest {
            attributes(
                "Implementation-Version" to project.version
            )
        }
    }

    shadowJar {
        archiveClassifier.set("")
    }

    build {
        dependsOn(shadowJar)
    }
}