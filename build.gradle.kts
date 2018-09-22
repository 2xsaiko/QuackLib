@file:Suppress("PropertyName")

import net.minecraftforge.gradle.user.IReobfuscator
import net.minecraftforge.gradle.user.ReobfTaskFactory
import net.minecraftforge.gradle.user.UserBaseExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val mod_version: String by extra
val mc_version: String by extra
val forge_version: String by extra
val mappings_version: String by extra
val kotlin_version: String by extra
val jei_version: String by extra
val forgelin_version: String by extra
val extmath_version: String by extra
val mcmp_version: String by extra
val cbmp_version: String by extra

val Project.minecraft: UserBaseExtension
  get() = extensions.getByName<UserBaseExtension>("minecraft")

buildscript {
  val kotlin_version: String by extra
  repositories {
    jcenter()
    mavenCentral()
    maven("http://files.minecraftforge.net/maven")
    maven("http://dl.bintray.com/kotlin/kotlin-eap")
  }
  dependencies {
    classpath("net.minecraftforge.gradle:ForgeGradle:2.3-SNAPSHOT")
    classpath(kotlin("gradle-plugin", "1.3-M1"))
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
  mappings = mappings_version
  isUseDepAts = true

  serverJvmArgs = serverJvmArgs + "-Dfml.coreMods.load=therealfarfetchd.quacklib.hax.QuackLibPlugin"
  clientJvmArgs = clientJvmArgs + "-Dfml.coreMods.load=therealfarfetchd.quacklib.hax.QuackLibPlugin"
}

tasks.withType<JavaCompile> {
  sourceCompatibility = "1.8"
  targetCompatibility = "1.8"
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
  kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.ExperimentalUnsignedTypes"
}

repositories {
  mavenCentral()
  maven("http://maven.shadowfacts.net/")
  maven("https://modmaven.k-4u.nl/")
  maven("https://maven.amadorn.es/")
  maven("http://dl.bintray.com/kotlin/kotlin-eap")
}

dependencies {
  compile(kotlin("stdlib-jdk8", kotlin_version))
  compile(kotlin("reflect", kotlin_version))

  runtimeOnly("net.shadowfacts", "Forgelin-EAP", forgelin_version)

  compile("therealfarfetchd.extmath", "extmath", extmath_version)

  // MCMP
  deobfCompile("MCMultiPart2", "MCMultiPart-exp", mcmp_version)

  // CBMP
  //  deobfCompile("codechicken", "ForgeMultipart", "$mc_version-$cbmp_version", classifier = "universal")

  // temp shit
  //  runtimeOnly("codechicken", "ChickenASM", "1.12-1.0.2.9")

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

tasks.getByName<Jar>("jar") {
  from(java.sourceSets["api"].output)

  manifest {
    attributes(mapOf(
      "FMLAT" to "quacklib_at.cfg",
      "FMLCorePlugin" to "therealfarfetchd.quacklib.hax.QuackLibPlugin",
      "FMLCorePluginContainsMod" to "true",
      "Maven-Artifact" to getMavenArtifactId(),
      "Timestamp" to System.currentTimeMillis()
    ))
  }
}

task("apiJar", Jar::class) {
  from(java.sourceSets["api"].output)

  classifier = "api"
}

reobf {
  create("apiJar")
}

tasks.getByName("build") {
  dependsOn("apiJar")
}

// java {
//   sourceSets {
//     "testmod" {
//       compileClasspath += "api"().output
//     }
//     "main" {
//       runtimeClasspath += "testmod"().output
//     }
//   }
// }

apply(from = "publish.gradle")

fun getMavenArtifactId(): String {
  var version = project.version.toString()
  if (System.getenv("BUILD_NUMBER") != null && System.getenv("SHOW_BUILD_NUMBER") != null)
    version += "_" + System.getenv("BUILD_NUMBER")
  return "$group:$name:$version"
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

fun reobf(op: NamedDomainObjectContainer<IReobfuscator>.() -> Unit) = configure(op)