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

import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
  alias(libs.plugins.kotlin.jvm) apply false
  `maven-publish`
  signing
  alias(libs.plugins.versions)
  alias(libs.plugins.dokka) apply false
  alias(libs.plugins.publish)
}

val githubAccount = "xemantic"
val isReleaseBuild = !project.version.toString().endsWith("-SNAPSHOT")
val githubActor: String? by project
val githubToken: String? by project
val signingKey: String? by project
val signingPassword: String? by project
val sonatypeUser: String? by project
val sonatypePassword: String? by project

println("""
  Project: ${project.name}
  Version: ${project.version}
  Release: $isReleaseBuild
""".trimIndent()
)

allprojects {
  repositories {
    mavenCentral()
  }
}

subprojects {

  if (project.name == "simple-java") {
    apply(plugin = "java")
  } else if (project.name != "demo") {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    tasks.named<KotlinCompilationTask<*>>("compileKotlin").configure {
      compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
      }
    }
  }

  if (project.name != "demo") {
    configure<JavaPluginExtension> {
      toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
      }
    }
  }

  if (project.name.startsWith(rootProject.name)) {

    apply(plugin = "maven-publish")
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "signing")

    configure<JavaPluginExtension> {
      withJavadocJar()
      withSourcesJar()
    }

    configure<PublishingExtension> {
      repositories {
        if (!isReleaseBuild) {
          maven {
            name = "GitHubPackages"
            setUrl("https://maven.pkg.github.com/$githubAccount/${rootProject.name}")
            credentials {
              username = githubActor
              password = githubToken
            }
          }
        }
      }
      publications {
        create<MavenPublication>("maven") {
          from(components["kotlin"])
          artifact(tasks.named<Jar>("javadocJar"))
          pom {
            name = "xemantic-kotlin-swing-dsl"
            description = "Kotlin goodies for Java Swing"
            url = "https://github.com/$githubAccount/${rootProject.name}"
            inceptionYear = "2020"
            organization {
              name = "Xemantic"
              url = "https://xemantic.com"
            }
            licenses {
              license {
                name = "GNU Lesser General Public License 3"
                url = "https://www.gnu.org/licenses/lgpl-3.0.en.html"
                distribution = "repo"
              }
            }
            scm {
              url = "https://github.com/$githubAccount/${rootProject.name}"
              connection = "scm:git:git:github.com/$githubAccount/${rootProject.name}.git"
              developerConnection = "scm:git:https://github.com/$githubAccount/${rootProject.name}.git"
            }
            ciManagement {
              system = "GitHub"
              url = "https://github.com/$githubAccount/${rootProject.name}/actions"
            }
            issueManagement {
              system = "GitHub"
              url = "https://github.com/$githubAccount/${rootProject.name}/issues"
            }
            developers {
              developer {
                id = "morisil"
                name = "Kazik Pogoda"
                email = "morisil@xemantic.com"
              }
            }
          }
        }
      }
    }

    if (isReleaseBuild) {
      configure<SigningExtension> {
        useInMemoryPgpKeys(
          signingKey,
          signingPassword
        )
        sign(publishing.publications["maven"])
      }
    }

    tasks {

      withType<Jar> {
        manifest {
          attributes(
            mapOf(
              "Implementation-Title" to project.name,
              "Implementation-Version" to project.version,
              "Implementation-Vendor" to "Xemantic",
              "Built-By" to "Gradle ${gradle.gradleVersion}",
              "Built-Date" to LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            )
          )
        }
        metaInf {
          from(rootProject.rootDir) {
            include("LICENSE")
          }
        }
      }

      named<Jar>("javadocJar") {
        from(named("dokkaJavadoc"))
      }

    }

  }

}

if (isReleaseBuild) {
  nexusPublishing {
    repositories {
      sonatype {  //only for users registered in Sonatype after 24 Feb 2021
        nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
        snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        username.set(sonatypeUser)
        password.set(sonatypePassword)
      }
    }
  }
}
