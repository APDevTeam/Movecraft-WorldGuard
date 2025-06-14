plugins {
    `java-library`
    `maven-publish`
    id("io.github.apdevteam.github-packages") version "1.2.2"
    id("io.papermc.hangar-publish-plugin") version "0.1.3"
}

repositories {
    gradlePluginPortal()
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven { githubPackage("apdevteam/movecraft")(this) }
    maven { githubPackage("apdevteam/movecraft-combat")(this) }
    maven { githubPackage("apdevteam/movecraft-repair")(this) }
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    api("org.jetbrains:annotations-java5:24.1.0")
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
    compileOnly("net.countercraft:movecraft:+")
    compileOnly("net.countercraft.movecraft.combat:movecraft-combat:+")
    compileOnly("net.countercraft.movecraft.repair:movecraft-repair:+")
    api("com.sk89q.worldedit:worldedit-core:7.2.9")
    api("com.sk89q.worldguard:worldguard-bukkit:7.0.7")
}

group = "net.countercraft.movecraft.worldguard"
version = "1.0.0_beta-5"
description = "Movecraft-WorldGuard"
java.toolchain.languageVersion = JavaLanguageVersion.of(21)

tasks.jar {
    archiveBaseName.set("Movecraft-WorldGuard")
    archiveClassifier.set("")
    archiveVersion.set("")
}

tasks.processResources {
    from(rootProject.file("LICENSE.md"))
    filesMatching("*.yml") {
        expand(mapOf("projectVersion" to project.version))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "net.countercraft.movecraft.worldguard"
            artifactId = "movecraft-worldguard"
            version = "${project.version}"

            artifact(tasks.jar)
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/apdevteam/movecraft-worldguard")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

hangarPublish {
    publications.register("plugin") {
        version.set(project.version as String)
        channel.set("Release")
        id.set("Airship-Pirates/Movecraft-WorldGuard")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms {
            register(io.papermc.hangarpublishplugin.model.Platforms.PAPER) {
                jar.set(tasks.jar.flatMap { it.archiveFile })
                platformVersions.set(listOf("1.20.6-1.21.5"))
                dependencies {
                    hangar("Movecraft") {
                        required.set(true)
                    }
                    url("WorldGuard", "https://dev.bukkit.org/projects/worldguard") {
                        required.set(true)
                    }
                    hangar("Movecraft-Combat") {
                        required.set(false)
                    }
                    hangar("Movecraft-Repair") {
                        required.set(false)
                    }
                }
            }
        }
    }
}
