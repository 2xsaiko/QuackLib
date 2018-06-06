fun setup() {
  defaultDeps = setOf("#main")

  api("tools")
  impl("internaltools", "#api_tools")
  component("core", "#api_tools", "#internaltools")

  defaultDeps += setOf("core", "#internaltools", "#api_tools")

  impl("classgen")
  component("block", "classgen")
  component("item", "classgen")
}

// --------------------------------------------------

buildscript {
  val kotlin_version: String by extra

  repositories {
    mavenCentral()
  }

  dependencies {
    classpath(kotlin("gradle-plugin", kotlin_version))
  }
}

plugins { `java-library` }

val jp = the<JavaPluginConvention>()

var defaultDeps: Set<String> = emptySet()
val missingDeps = mutableMapOf<String, MutableSet<SourceSet>>().withDefault { mutableSetOf() }

fun component(name: String, vararg dependsOn: String): SourceSet {
  val api = api(name, *dependsOn)
  return impl(name, *dependsOn) {
    depend(api)
  }
}

fun api(name: String, vararg dependsOn: String, configuration: SourceSet.() -> Unit = {}): SourceSet = new("api_$name") {
  val deps = (defaultDeps + dependsOn)
    .map {
      if (it.startsWith('#')) it.drop(1)
      else "api_$it"
    }

  mvsrc("api/$name")
  deps.forEach { depend(it) }
  configuration()
}

fun impl(name: String, vararg dependsOn: String, configuration: SourceSet.() -> Unit = {}): SourceSet = new(name) {
  val deps = (defaultDeps + dependsOn)
    .map {
      if (it.startsWith('#')) it.drop(1)
      else it
    }

  mvsrc("impl/$name")
  deps.forEach { depend(it) }
  configuration()
}

fun SourceSet.mvsrc(to: String) {
  val re = "^.*/src/$name/(.*)$".toRegex() // FIXME this will probably not work on windows
  val src = allSource.srcDirs.map { re.matchEntire(it.path)!!.groupValues[1] }
  val j = src - "resources"
  val r = src - j
  java.setSrcDirs(j.map { "src/$to/$it" })
  resources.setSrcDirs(r.map { "src/$to/$it" })
}

fun new(name: String, configuration: SourceSet.() -> Unit = {}): SourceSet {
  lateinit var ss: SourceSet
  configure<JavaPluginConvention> {
    sourceSets {
      ss = name(configuration)
      missingDeps.getValue(name).forEach {
        it.depend(ss)
      }
      missingDeps -= name
    }
  }
  return ss
}

fun SourceSet.depend(name: String) {
  if (name in jp.sourceSets.names) depend(jp.sourceSets[name])
  else missingDeps.getOrPut(name, { mutableSetOf() }) += this
}

fun SourceSet.depend(other: SourceSet) {
  compileClasspath += other.compileClasspath + other.output
}

setup()

if (missingDeps.isNotEmpty())
  println("Missing source sets: ${missingDeps.keys}. This is not necessarily a bad thing!")