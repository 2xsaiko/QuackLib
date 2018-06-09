@file:Suppress("PropertyName")

import net.minecraftforge.gradle.user.UserBaseExtension
import org.gradle.jvm.tasks.Jar

val mod_version: String by extra
val mc_version: String by extra
val forge_version: String by extra
val mappings_version: String by extra
val kotlin_version: String by extra
val jei_version: String by extra
val forgelin_version: String by extra

val Project.minecraft: UserBaseExtension
  get() = extensions.getByName<UserBaseExtension>("minecraft")

buildscript {
  val kotlin_version: String by extra
  repositories {
    jcenter()
    mavenCentral()
    maven("http://files.minecraftforge.net/maven")
  }
  dependencies {
    classpath("net.minecraftforge.gradle:ForgeGradle:2.3-SNAPSHOT")
    classpath(kotlin("gradle-plugin", kotlin_version))
  }
}

plugins {
  `java-library`
}

apply {
  plugin("net.minecraftforge.gradle.forge")
  plugin("kotlin")
}

version = mod_version
group = "therealfarfetchd.quacklib"

minecraft {
  version = "$mc_version-$forge_version"
  runDir = "run"
  mappings = "snapshot_$mappings_version"
  isUseDepAts = true
}

tasks.withType<JavaCompile> {
  sourceCompatibility = "1.8"
  targetCompatibility = "1.8"
}

repositories {
  mavenCentral()
  maven("https://modmaven.k-4u.nl/")
  maven("http://maven.shadowfacts.net/")
}

dependencies {
  implementation(kotlin("stdlib-jdk8", kotlin_version))
  implementation(kotlin("reflect", kotlin_version))

  runtimeOnly("net.shadowfacts", "Forgelin", forgelin_version)

  runtimeOnly("mezz.jei", "jei_$mc_version", jei_version)
}

tasks.withType<Jar> {
  inputs.properties += "version" to project.version
  inputs.properties += "mcversion" to project.minecraft.version

  baseName = "quacklib"

  filesMatching("/mcmod.info") {
    expand(mapOf(
      "version" to project.version,
      "mcversion" to project.minecraft.version
    ))
  }
}

java {
  sourceSets {
    "api" {
      compileClasspath += "main"().compileClasspath
    }
  }
  manifest {
    attributes(mapOf(
      "FMLAT" to "quacklib_at.cfg"
    ))
  }
}

fun DependencyHandler.deobfCompile(
  group: String,
  name: String,
  version: String? = null,
  configuration: String? = null,
  classifier: String? = null,
  ext: String? = null): ExternalModuleDependency =
  create(group, name, version, configuration, classifier, ext).apply { add("deobfCompile", this) }

fun DependencyHandler.deobfCompile(dependencyNotation: Any): Dependency? =
  add("deobfCompile", dependencyNotation)

fun minecraft(op: UserBaseExtension.() -> Unit) = configure(op)