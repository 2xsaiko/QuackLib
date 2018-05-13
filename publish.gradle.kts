// Git Publish v1.0.4
// by therealfarfetchd

import org.gradle.jvm.tasks.Jar

// Set this to whatever Git repository you want. You must be able to push to it.
val RepoURL = "ssh://git@github.com/therealfarfetchd/maven.git"
val RepoBranch = "master"

val LocalRepoURL = File(buildDir, "maven")

buildscript {
  val kotlin_version: String? by extra
  repositories { mavenCentral() }
  dependencies { classpath(kotlin("gradle-plugin", kotlin_version ?: "1.2.41")) }
}

plugins { java }
apply { plugin("maven-publish") }

val sourcesJar by tasks.creating(Jar::class) {
  classifier = "sources"
  from(the<JavaPluginConvention>().sourceSets["main"].allSource)
}

configure<PublishingExtension> {
  repositories {
    maven { url = LocalRepoURL.toURI() }
  }
  (publications) {
    "mavenJava"(MavenPublication::class) {
      from(components["java"])
      artifact(sourcesJar)
    }
  }
}

tasks {
  "setupLocalRepo"(Exec::class) {
    group = "git"
    onlyIf { !LocalRepoURL.exists() }
    LocalRepoURL.parentFile.mkdirs()
    commandLine = listOf("git", "clone", RepoURL, "-b", RepoBranch, LocalRepoURL.path)
  }

  "updateLocalRepo"(Exec::class) {
    group = "git"
    dependsOn("setupLocalRepo")
    workingDir = LocalRepoURL
    commandLine = listOf("git", "pull")
  }

  "addFilesLocal"(Exec::class) {
    group = "git"
    dependsOn("setupLocalRepo")
    workingDir = LocalRepoURL
    commandLine = listOf("git", "add", ".")
  }

  "commitFilesLocal"(Exec::class) {
    group = "git"
    dependsOn("addFilesLocal")
    workingDir = LocalRepoURL
    commandLine = listOf("git", "commit", "-m", "Build ${project.name}")
  }

  "updateRemoteRepo"(Exec::class) {
    group = "git"
    dependsOn("commitFilesLocal")
    workingDir = LocalRepoURL
    commandLine = listOf("git", "push")
  }

  "publish" {
    dependsOn("updateLocalRepo")
    finalizedBy("updateRemoteRepo")
    mustRunAfter("updateLocalRepo")
  }
}
