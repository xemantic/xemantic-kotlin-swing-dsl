/*
 * This file is part of xemantic-kotlin-swing-dsl - Kotlin goodies for Java Swing.
 *
 * Copyright (C) 2024  Kazimierz Pogoda
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
  `maven-publish`
  signing
  alias(libs.plugins.versions)
}

allprojects {
  repositories {
    mavenCentral()
  }

}

publishing {

  publications {
    create<MavenPublication>("mavenJava") {
      groupId = "com.xemantic.kotlin"
      //from(components["kotlin"])
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
    maven {
      name = "GitHubPackages"
      setUrl("https://maven.pkg.github.com/xemantic/xemantic-kotlin-swing-dsl")
      credentials {
        username = System.getenv("GITHUB_ACTOR")
        password = System.getenv("GITHUB_TOKEN")
      }
    }
//      maven {
//        name = "OSSRH"
//        setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/")
//        credentials {
//          username = System.getenv("MAVEN_USERNAME")
//          password = System.getenv("MAVEN_PASSWORD")
//        }
//      }
  }

}



signing {
  if (
    project.hasProperty("signing.keyId")
    && project.hasProperty("signing.password")
    && project.hasProperty("signing.secretKeyRingFile")
  ) {
    sign(publishing.publications)
  }
}
