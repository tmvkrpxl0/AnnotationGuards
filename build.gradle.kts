import java.time.LocalDateTime

plugins {
    id("eclipse")
    id("maven-publish")
    id("net.minecraftforge.gradle") version "5.1.+"
}

group = "com.tmvkrpxl0"
version = "1.0-SNAPSHOT"

val mc_version: String by project
val forge_version: String by project

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

configurations {
    apiElements {
        artifacts.clear()
    }
    runtimeElements {
        setExtendsFrom(emptySet())
        // Publish the jarJar
        artifacts.clear()
    }
}

minecraft {
    mappings("official", mc_version)
}

repositories {
    mavenLocal()
}

dependencies {
    minecraft("net.minecraftforge:forge:1.19.3-44.0.18")
}

tasks.withType<Jar> {
    manifest {
        attributes(
            "Specification-Title" to "Forge Annotation Guards",
            "Specification-Vendor" to "Forge",
            "Specification-Version" to "1",
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "tmvkrpxl0",
            "Implementation-Timestamp" to LocalDateTime.now(),
            "FMLModType" to "GAMELIBRARY"
        )
    }

    finalizedBy("reobfJar")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "annotationguards"
            from(components["java"])
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8" // Use the UTF-8 charset for Java compilation
}