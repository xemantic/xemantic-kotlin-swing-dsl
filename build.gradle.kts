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

plugins {
  alias(libs.plugins.kotlin.jvm) apply false
  `maven-publish`
  signing
  alias(libs.plugins.versions)
  alias(libs.plugins.dokka) apply false
  alias(libs.plugins.publish)
}

val githubAccount = "xemantic"

allprojects {
  repositories {
    mavenCentral()
  }
}

subprojects {

  afterEvaluate {
    if (project.name != "demo") {
      configure<JavaPluginExtension> {
        toolchain {
          languageVersion.set(JavaLanguageVersion.of(17))
        }
      }
      if (project.name != "simple-java") {
        tasks.named<KotlinCompilationTask<*>>("compileKotlin").configure {
          compilerOptions {
            apiVersion.set(KotlinVersion.KOTLIN_2_0)
          }
        }
      }
    }
  }

  if (project.name.startsWith(rootProject.name)) {

    apply(plugin = "maven-publish")
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.dokka")

    configure<JavaPluginExtension> {
      withJavadocJar()
      withSourcesJar()
    }

    afterEvaluate {

      tasks {

        withType<Jar> {
          manifest {
            attributes(
              mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
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

      configure<PublishingExtension> {
        repositories {
          maven {
            name = "GitHubPackages"
            setUrl("https://maven.pkg.github.com/$githubAccount/${rootProject.name}")
            credentials {
              username = System.getenv("GITHUB_ACTOR")
              password = System.getenv("GITHUB_TOKEN")
            }
          }
        }
        publications {
          create<MavenPublication>("maven") {
            from(components["kotlin"])
            artifact(tasks.named<Jar>("javadocJar"))
            pom {
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
    }

  }

}

signing {
  if (
    project.hasProperty("signing.keyId")
    && project.hasProperty("signing.password")
    && project.hasProperty("signing.secretKeyRingFile")
  ) {
    sign(publishing.publications["maven"])
  }
}

nexusPublishing {
  repositories {
    sonatype {  //only for users registered in Sonatype after 24 Feb 2021
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
      username.set(System.getenv("SONATYPE_USER"))
      password.set(System.getenv("SONATYPE_PASSWORD"))
    }
  }
}
