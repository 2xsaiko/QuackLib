import net.minecraftforge.gradle.user.patcherUser.forge.ForgeExtension
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val mod_name: String by extra
val mod_version: String by extra
val mc_version: String by extra
val forge_version: String by extra
val mappings_version: String by extra
val kotlin_version: String by extra
val forgelin_version: String by extra
val mcmultipart_version: String by extra
val jei_version: String by extra

val Project.minecraft: ForgeExtension
  get() = extensions.getByName<ForgeExtension>("minecraft")

group = "therealfarfetchd.$mod_name"

apply { from("publish.gradle") }

buildscript {
  val kotlin_version: String by extra
  repositories {
    jcenter()
    mavenCentral()
    maven { setUrl("http://files.minecraftforge.net/maven") }
  }
  dependencies {
    classpath("net.minecraftforge.gradle:ForgeGradle:2.3-SNAPSHOT")
    classpath(kotlin("gradle-plugin", kotlin_version))
  }
}

plugins {
  java
}

apply {
  plugin("net.minecraftforge.gradle.forge")
  plugin("kotlin")
}

version = mod_version
group = "therealfarfetchd.quacklib"

repositories {
  maven { setUrl("https://modmaven.k-4u.nl/") }
  maven { setUrl("http://maven.shadowfacts.net/") }
}

dependencies {
  runtimeOnly("net.shadowfacts", "Forgelin", forgelin_version)
  compileOnly(kotlin("stdlib-jre8", kotlin_version))
  compileOnly(kotlin("reflect", kotlin_version))

  deobfCompile("MCMultiPart2", "MCMultiPart", mcmultipart_version)

  runtimeOnly("mezz.jei", "jei_$mc_version", jei_version)
  deobfProvided("mezz.jei", "jei_$mc_version", jei_version, classifier = "api")
}

configure<ForgeExtension> {
  version = "$mc_version-$forge_version"
  runDir = "run"
  mappings = mappings_version
}

tasks.withType<JavaCompile> {
  sourceCompatibility = "1.8"
  targetCompatibility = "1.8"
}

tasks.withType<KotlinCompile> {
  sourceCompatibility = "1.8"
  targetCompatibility = "1.8"
}

tasks.withType<Jar> {
  inputs.properties += "version" to project.version
  inputs.properties += "mcversion" to project.minecraft.version

  baseName = mod_name

  filesMatching("/mcmod.info") {
    expand(mapOf(
      "version" to project.version,
      "mcversion" to project.minecraft.version
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

fun DependencyHandler.deobfProvided(
  group: String,
  name: String,
  version: String? = null,
  configuration: String? = null,
  classifier: String? = null,
  ext: String? = null): ExternalModuleDependency =
  create(group, name, version, configuration, classifier, ext).apply { add("deobfProvided", this) }