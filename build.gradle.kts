import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.2.2"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
    id("xyz.jpenilla.run-paper") version "3.0.0"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.3.1"
    id("xyz.jpenilla.gremlin-gradle") version "0.0.9"
}

group = "de.mcmdev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "helpchat"
        url = uri("https://repo.helpch.at/snapshots")
    }
    maven {
        name = "scarsz"
        url = uri("https://nexus.scarsz.me/content/groups/public/")
    }
    // Needed for OpenInv - Yuck!
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
    runtimeDownload("space.arim.dazzleconf:dazzleconf-core:2.0.0-M1")
    runtimeDownload("space.arim.dazzleconf:dazzleconf-yaml:2.0.0-M1")
    runtimeDownload("space.arim.injector:injector:1.1.0-RC2")
    runtimeDownload("jakarta.inject:jakarta.inject-api:2.0.1")
    compileOnly("me.clip:placeholderapi:2.11.7-DEV-212")
    compileOnly("com.github.Jikoo:OpenInv:5.3.0")
    compileOnly("com.discordsrv:discordsrv:1.28.0")
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

paperPluginYaml {
    main = "de.mcmdev.vanish.VanishPlugin"
    loader = "xyz.jpenilla.gremlin.runtime.platformsupport.DefaultsPaperPluginLoader"
    apiVersion = "1.21"
    foliaSupported = true

    dependencies {
        server {
            register("PlaceholderAPI") {
                required = true
                load = PaperPluginYaml.Load.BEFORE
            }
            register("OpenInv") {
                required = false
                load = PaperPluginYaml.Load.BEFORE
            }
            register("DiscordSRV") {
                required = false
                load = PaperPluginYaml.Load.BEFORE
            }
        }
    }

    permissions {
        register("vanish.command")
        register("vanish.command.setlevel")
        register("vanish.see.<level>")
        register("vanish.use.<level>")
        register("vanish.protection.block_place")
        register("vanish.protection.block_break")
        register("vanish.protection.entity_damage")
        register("vanish.protection.player_interact")
        register("vanish.protection.player_drop")
        register("vanish.hostname")
    }
}

runPaper {
    folia {
        registerTask()
    }
}

tasks.getByName("runFolia", RunServer::class) {
    downloadPlugins {
        url("https://ci.lucko.me/job/LuckPerms-Folia/9/artifact/bukkit/loader/build/libs/LuckPerms-Bukkit-5.5.11.jar")
        url("https://github.com/Jikoo/OpenInv/releases/download/5.1.15/OpenInv.jar")
        url("https://ci.extendedclip.com/job/PlaceholderAPI/212/artifact/build/libs/PlaceholderAPI-2.11.7-DEV-212.jar")
    }
}

tasks.assemble {
    dependsOn(tasks.writeDependencies)
}

configurations.compileOnly {
    extendsFrom(configurations.runtimeDownload.get())
}
configurations.testImplementation {
    extendsFrom(configurations.runtimeDownload.get())
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}