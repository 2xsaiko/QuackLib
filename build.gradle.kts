import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import net.minecraftforge.gradle.kotlin.*

val kotlin_version: String by extra
val mappings_version: String by extra
val mc_version: String by extra
val mod_version: String by extra
val forge_version: String by extra
val jei_version: String by extra

buildscript {
  val kotlin_version: String by extra

  repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.gradle.org/gradle/libs-releases-local/")
  }

  dependencies {
    classpath(gradleApi())
    classpath(kotlin("gradle-plugin", kotlin_version))
    classpath("net.minecraftforge.gradle:forgegradle-kotlin:1.0.+")
  }
}

plugins { `java-library` }
apply(plugin = "kotlin")
apply(plugin = "net.minecraftforge.gradle-kotlin")

group = "therealfarfetchd.quacklib"
version = mod_version

repositories {
  mavenCentral()
  forgeMaven()
  minecraftMaven()

  maven("https://modmaven.k-4u.nl/")

  mappings(mcp())
}

dependencies {
  implementation(kotlin("stdlib-jdk8", kotlin_version))

  implementation(forge(forge_version))
  implementation(remap("net.minecraft", "minecraft", mc_version, classifier = "server-pure", mapping = "notch-mcp", remapTransitives = false))
  implementation(remap("net.minecraft", "minecraft", mc_version, classifier = "client", mapping = "notch-mcp", remapTransitives = false))

  runtimeOnly(deobf("mezz.jei", "jei_$mc_version", jei_version))

  // maybe? not now
  // testCompile("junit", "junit", "4.12")
}

forgegradle {
  minecraft.version = mc_version
  mappings {
    channel = "snapshot"
    version = mappings_version
  }
}

java.sourceSets {
  val api = "api"()
  "main" {
    compileClasspath += api.compileClasspath + api.output
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}