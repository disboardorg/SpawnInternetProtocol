plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.disboard"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    mavenCentral()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT")
    implementation("io.ipinfo:ipinfo-api:2.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    named<ProcessResources>("processResources") {
        inputs.property("version", project.version)
        from(sourceSets.main.get().resources.srcDirs) {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            include("plugin.yml")
            expand("version" to project.version)
        }
    }

    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveFileName.set("${project.name}-${project.version}.jar")
        minimize()
    }

    named("build") {
        dependsOn("shadowJar")
    }
}
