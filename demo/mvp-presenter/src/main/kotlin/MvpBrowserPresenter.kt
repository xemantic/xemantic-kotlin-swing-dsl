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
package com.xemantic.kotlin.swing.demo.mvp

import com.xemantic.kotlin.swing.Action
import com.xemantic.kotlin.swing.swingScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Abstract representation of all the interactions the [BrowserPresenter]
 * might have with the [BrowserView]. This way the view can be easily mocked and
 * presenter can be tested with assertions run against such a mock.
 * Passive view can be also implemented in any UI toolkit on any
 * supported platform. It is designed according to the Model-View-Presenter
 * pattern.
 *
 * All the events coming from the view are represented as [Flow]s
 * which can be subscribed to. A mutable view state is represented as
 * simple `var` properties, but it could be also a function call.
 */
interface BrowserView {
  val urlEdits: Flow<String>
  val goActions: Flow<Action>
  val urlActions: Flow<Action>
  var goActionEnabled: Boolean
  var content: String
  var url: String
}

/**
 * Represents asynchronous retrieval of the data from the Internet.
 */
interface Internet {
  suspend fun download(url: String): String
}

class BrowserPresenter(
  private val view: BrowserView,
  scope: CoroutineScope,
  internet: Internet
) {

  var loading = false
  var url = ""
    private set

  private val urlFlow = MutableSharedFlow<String>()

  init {
    scope.swingScope {
      view.urlEdits.listen("urls") {
        url = it
        view.goActionEnabled = url.isNotBlank()
      }
      merge(
        urlFlow,
        view.goActions.map { url },
        view.urlActions.filter { url.isNotBlank() }.map { url }
      )
        .filterNot { loading }
        .onEach {
          loading = true
          view.goActionEnabled = false
        }
        .map {
          try {
            internet.download(url)
          } catch (e : Exception) {
            "$e"
          }
        }
        .listen("content") {
          loading = false
          view.goActionEnabled = true
          view.content = it
        }
    }
  }

  suspend fun open(url: String) {
    this.url = url
    view.url = url
    urlFlow.emit(url)
  }

}
