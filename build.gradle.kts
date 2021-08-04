/*
 * This file is part of xemantic-kotlin-swing-dsl - Kotlin goodies for Java Swing.
 *
 * Copyright (C) 2021  Kazimierz Pogoda
 *
 * xemantic-kotlin-swing-dsl is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * xemantic-kotlin-swing-dsl is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with xemantic-kotlin-swing-dsl. If not,
 * see <https://www.gnu.org/licenses/>.
 */

plugins {
  kotlin("jvm") version "1.5.21"
  signing
  `maven-publish`
}

group = "com.xemantic.kotlin"
version = "1.0-SNAPSHOT"

val javaCompatibilityVersion = "1.8"
val kotlinCompatibilityVersion = "1.5"
val reaktiveVersion = "1.1.21"

repositories {
  mavenCentral()
}

dependencies {
  implementation("com.badoo.reaktive:reaktive:$reaktiveVersion")
}

tasks {

  compileKotlin {
    kotlinOptions {
      jvmTarget = javaCompatibilityVersion
      apiVersion = kotlinCompatibilityVersion
      languageVersion = kotlinCompatibilityVersion
    }
    sourceCompatibility = javaCompatibilityVersion
  }

}

publishing {

  publications {
    create<MavenPublication>("mavenJava") {
      pom {
        name.set("xemantic-kotlin-swing-dsl")
        description.set("Kotlin goodies for Java Swing")
        url.set("https://github.com/xemantic/xemantic-kotlin-swing-dsl")

        licenses {
          license {
            name.set("GNU Lesser General Public License 3")
            url.set("https://www.gnu.org/licenses/lgpl-3.0.en.html")
          }
        }
        developers {
          developer {
            id.set("morisil")
            name.set("Kazik Pogoda")
            email.set("morisil@xemantic.com")
          }
        }
        scm {
          url.set("https://github.com/xemantic/xemantic-kotlin-swing-dsl")
        }
      }
    }
  }

  repositories {
//    maven {
//      name = "OSSRH"
//      setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
//      credentials {
//        username = System.getenv("MAVEN_USERNAME")
//        password = System.getenv("MAVEN_PASSWORD")
//      }
//    }
    maven {
      name = "GitHubPackages"
      setUrl("https://maven.pkg.github.com/xemantic/xemantic-kotlin-swing-dsl")
      credentials {
        username = System.getenv("GITHUB_ACTOR")
        password = System.getenv("GITHUB_TOKEN")
      }
    }
  }

  publications.withType<MavenPublication> {

    // Stub javadoc.jar artifact
//    artifact(javadocJar.get())


  }
}

signing {
//  sign(publishing.publications)
}
