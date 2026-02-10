plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
}

group = "me.noibecoded"
version = project.property("version")!!

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.3-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("net.kyori:adventure-api:4.18.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.18.0")

    implementation("mysql:mysql-connector-java:8.0.33")

    annotationProcessor("org.spigotmc:spigot-api:1.21.3-R0.1-SNAPSHOT")
}

tasks {
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        filesMatching("plugin.yml") {
            expand("version" to version)
        }
    }

    jar {
        archiveFileName.set("XpUltimate-${version}.jar")
    }

    shadowJar {
        archiveFileName.set("XpUltimate-${version}-shaded.jar")
        minimize()
        relocate("net.kyori", "me.noibecoded.xpultimate.libs.kyori")
        relocate("com.mysql", "me.noibecoded.xpultimate.libs.mysql")
    }

    build {
        dependsOn(shadowJar)
    }
}
