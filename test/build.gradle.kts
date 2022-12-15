plugins {
    id("eclipse")
    id("net.minecraftforge.gradle") version "5.1.+"
}

group = rootProject.group
version = rootProject.version

val mc_version: String by project
val forge_version: String by project

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

minecraft {
    mappings("official", mc_version)
}

dependencies {
    minecraft("net.minecraftforge:forge:1.19.3-44.0.18")
    annotationProcessor(rootProject)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8" // Use the UTF-8 charset for Java compilation
}