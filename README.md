# xemantic-kotlin-swing-dsl

_Express your Swing code easily in Kotlin_

[<img alt="Maven Central Version" src="https://img.shields.io/maven-central/v/com.xemantic.kotlin/xemantic-kotlin-swing-dsl-core?style=for-the-badge">](https://central.sonatype.com/namespace/com.xemantic.kotlin)
![GitHub Release Date](https://img.shields.io/github/release-date/xemantic/xemantic-kotlin-swing-dsl?style=for-the-badge)
[<img alt="license" src="https://img.shields.io/github/license/thelumiereguy/ShaderShowcaseApp?color=blue&style=for-the-badge">]()

![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/xemantic/xemantic-kotlin-swing-dsl/build-main.yml?style=for-the-badge)
![GitHub branch check runs](https://img.shields.io/github/check-runs/xemantic/xemantic-kotlin-swing-dsl/main?style=for-the-badge)
![GitHub commits since latest release](https://img.shields.io/github/commits-since/xemantic/xemantic-kotlin-swing-dsl/latest?style=for-the-badge)
![GitHub last commit](https://img.shields.io/github/last-commit/xemantic/xemantic-kotlin-swing-dsl?style=for-the-badge)

![GitHub contributors](https://img.shields.io/github/contributors/xemantic/xemantic-kotlin-swing-dsl?style=for-the-badge)
![GitHub commit activity](https://img.shields.io/github/commit-activity/t/xemantic/xemantic-kotlin-swing-dsl?style=for-the-badge)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/xemantic/xemantic-kotlin-swing-dsl?style=for-the-badge)
![GitHub Created At](https://img.shields.io/github/created-at/xemantic/xemantic-kotlin-swing-dsl?style=for-the-badge)
![kotlin version](https://img.shields.io/badge/dynamic/toml?url=https%3A%2F%2Fraw.githubusercontent.com%2Fxemantic%2Fxemantic-kotlin-swing-dsl%2Fmain%2Fgradle%2Flibs.versions.toml&query=versions.kotlin&style=for-the-badge&label=kotlin)
![kotlinx-coroutines version](https://img.shields.io/badge/dynamic/toml?url=https%3A%2F%2Fraw.githubusercontent.com%2Fxemantic%2Fxemantic-kotlin-swing-dsl%2Fmain%2Fgradle%2Flibs.versions.toml&query=versions.kotlinxCoroutines&style=for-the-badge&label=kotlinx-coroutines)

[![](https://dcbadge.limes.pink/api/server/https://discord.gg/vQktqqN2Vn)](https://discord.gg/vQktqqN2Vn)
![Discord](https://img.shields.io/discord/811561179280965673?style=for-the-badge&link=https%3A%2F%2Fdiscord.gg%2FvQktqqN2Vn)
[<img alt="X (formerly Twitter) Follow" src="https://img.shields.io/twitter/follow/KazikPogoda?link=https%3A%2F%2Fx.com%2FKazikPogoda">
](https://x.com/KazikPogoda)

## Why?

Kotlin provides incredible language sugar over pure Java. Using Kotlin
instead of Java for writing Swing UI already makes the code more concise, but
**what if the Swing was written from scratch, to provide Kotlin-idiomatic way of doing things?**
This is the intent behind the `xemantic-kotlin-swing-dsl` library - to deliver a
[Domain Specific Language](https://en.wikipedia.org/wiki/Domain-specific_language)
for building Swing based UI and react to UI events by utilizing
[Kotlin coroutines](https://kotlinlang.org/docs/coroutines-overview.html). 

## Usage

Add to your `build.gradle.kts`:

```kotlin
dependencies {
  implementation("com.xemantic.kotlin:xemantic-kotlin-swing-dsl-core:1.0")
  runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.1")
}
```

:warning: the `kotlinx-coroutines-swing` version should be aligned with the version of other coroutine
libraries used in your project. If unsure, find the latest version of the `kotlinx-coroutines` library.

## Example

Here is a simple internet browser. For the sake of example, instead of rendering
the full HTML, it will just download a content from provided URL address
and display it as a text.

```kotlin
import com.xemantic.kotlin.swing.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.awt.Dimension
import java.net.URI

fun main() = MainWindow("My Browser") { window ->
  val urlBox = TextField()
  val goButton = Button("Go!") { isEnabled = false }
  val contentBox = TextArea()

  urlBox.textChanges.listen { url ->
    goButton.isEnabled = url.isNotBlank()
  }

  merge(
    goButton.actionEvents,
    urlBox.actionEvents
  )
    .filter { goButton.isEnabled }
    .onEach { goButton.isEnabled = false }
    .map { urlBox.text }
    .flowOn(Dispatchers.Main)
    .map {
      try {
        URI(it).toURL().readText()
      } catch (e : Exception) {
        e.message
      }
    }
    .flowOn(Dispatchers.IO)
    .listen {
      contentBox.text = it
      goButton.isEnabled = true
    }

  window.preferredSize = Dimension(300, 300)

  BorderPanel {
    north {
      Border.empty(4) {
        BorderPanel {
          layout {
            hgap = 4
            vgap = 4
          }
          west { Label("URL") }
          center { urlBox }
          east { goButton }
        }
      }
    }
    center { ScrollPane { contentBox } }
  }
}
```

When run it will produce:

![example app image](docs/xemantic-kotlin-swing-dsl-example.png)

### Notable conventions

* No `J` prefix in UI component names (historically `J` was added to differentiate
  Swing components from AWT components and is mostly irrelevant for modern purposes).
* Main JFrame is created with the `MainWindow` builder, which also takes care of setting
  up the `SwingScope` holding a coroutine scope bound to Swing's event dispatcher thread.
* Instead of event listeners (callbacks), events are delivered through `Flow`s. The `listen()`
  function collects the flow in the newly launched coroutine, which is cancelled when the window
  is closed.
* Other coroutine dispatchers, like `IO`, can be used in the event processing pipeline
  by adding the `flowOn`. This makes the cumbersome `SwingWorker` obsolete.
* Each UI component can be immediately configured with direct access to its properties.
* Panels are specified with special builders, allowing intuitive development of the
  component tree.

### Key benefits

* compact UI code with minimal verbosity
* declarative instead of imperative UI building
* reactive event handling using `Flow`s, 

The code above is taken from the
[MyBrowserKotlin](demo/simple-kotlin-dsl/src/main/kotlin/MyBrowserKotlin.kt) demo.

### How would it look in pure Java Swing?

For the sake of comparison the
[MyBrowserJava](demo/simple-java/src/main/java/com/xemantic/kotlin/swing/demo/MyBrowserJava.java)
demo implements exactly the same "browser" in pure Java.

## Other examples

The [demo](demo) folder contains additional examples.

## Testing

This library is also supporting testing of presenters, if your code adheres to Model View Presenter
principles. You can check an example in the [mvp-presenter demo](demo/mvp-presenter) module.

Add this dependency to your `build.gradle.kts` for testing support:

```kotlin
dependencies {
  testImplementation("com.xemantic.kotlin:xemantic-kotlin-swing-dsl-test:1.0")
}
```
