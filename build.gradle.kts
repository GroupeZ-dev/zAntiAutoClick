import org.gradle.kotlin.dsl.invoke

plugins {
    `java-library`
    id("com.gradleup.shadow") version "9.0.0-beta11"
}

group = "fr.maxlego08.antiautoclick"
version = "1.0.2"

extra.set("targetFolder", file("target/"))
extra.set("apiFolder", file("target-api/"))
extra.set("classifier", System.getProperty("archive.classifier"))
extra.set("sha", System.getProperty("github.sha"))

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "com.gradleup.shadow")

    group = "fr.maxlego08.antiautoclick"
    version = rootProject.version

    repositories {
        mavenLocal()
        mavenCentral()

        maven {
            name = "groupezReleases"
            url = uri("https://repo.groupez.dev/releases")
        }
        maven {
            name = "groupezSnapshots"
            url = uri("https://repo.groupez.dev/snapshots")
        }
        maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
    }

    tasks.javadoc {
        options.encoding = "UTF-8"
        if (JavaVersion.current().isJava9Compatible) (options as StandardJavadocDocletOptions).addBooleanOption(
            "html5", true
        )
    }

    dependencies {
        compileOnly("org.spigotmc:spigot-api:1.21.5-R0.1-SNAPSHOT")
        compileOnly("fr.maxlego08.menu:zmenu-api:b66090d")
        compileOnly("com.github.retrooper:packetevents-spigot:2.8.0")
        implementation("fr.maxlego08.sarah:sarah:1.18")
    }
}

repositories {
    maven(url = "https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    api(projects.api)
    // api(projects.hooks)
    implementation("de.tr7zw:item-nbt-api:2.15.0")
}

tasks {
    shadowJar {

        relocate("fr.maxlego08.sarah", "fr.maxlego08.autoclick.libs")

        rootProject.extra.properties["sha"]?.let { sha ->
            archiveClassifier.set("${rootProject.extra.properties["classifier"]}-${sha}")
        } ?: run {
            archiveClassifier.set(rootProject.extra.properties["classifier"] as String?)
        }
        destinationDirectory.set(rootProject.extra["targetFolder"] as File)
    }

    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.release = 21
    }

    processResources {
        from("resources")
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
}
