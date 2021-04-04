plugins {
  kotlin("jvm") version "1.4.32"
  id("org.jetbrains.dokka") version "1.4.30"
}

group = "com.xemantic.kotlin"
version = "1.0-SNAPSHOT"

val javaVersion = "1.8"
val kotlinVersion = "1.4"


repositories {
  mavenCentral()
  jcenter()
}

dependencies {
  implementation("io.reactivex.rxjava3:rxjava:3.0.11")
}

tasks {
  compileKotlin {
    kotlinOptions {
      jvmTarget = javaVersion
      apiVersion = kotlinVersion
      languageVersion = kotlinVersion
    }
    sourceCompatibility = javaVersion
  }
}
