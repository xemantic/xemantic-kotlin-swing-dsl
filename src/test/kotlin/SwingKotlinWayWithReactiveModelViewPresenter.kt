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

package com.xemantic.kotlin.swing

import com.badoo.reaktive.observable.*
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.scheduler.ioScheduler
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.SwingConstants

/**
 * Abstract representation of all the interactions the Presenter
 * might have with the View. This way view can be easily mocked and
 * presenter can be tested with assertions run against such a mock.
 * Passive view can be also easily implemented in any UI toolkit on
 * any platform. Therefore so called Model-View-Presenter pattern
 * might be very powerful with Kotlin Multiplatform Projects.
 *
 * All the events coming from the view are represented as [Observable]s
 * which can be subscribed to. A mutable view state is represented as
 * simple `var` properties, but it can be also a function.
 */
interface BrowserView {
  val urlEditEvents: Observable<String>
  val goActions: Observable<ActionEvent>
  var goActionEnabled: Boolean
  var content: String
}

/**
 * The presenter is mostly defining streams of events which
 * will be wired together once [Presenter.start] is called.
 *
 * @param scheduler the scheduler to use for Swing state mutations
 *          if we are waiting on another scheduler we whould observe back
 *          on this one.
 */
class BrowserPresenter(scheduler: Scheduler) : Presenter<BrowserView>({
  observe {
    urlEditEvents.doOnAfterNext { url ->
      goActionEnabled = url.isNotBlank()
      content = "Will try: $url"
    }
  }
  observe {
    goActions
      .withLatestFrom(urlEditEvents) { _, url: String -> url }
      .doOnAfterNext { url ->
        goActionEnabled = false
        content = "Loading: $url"
      }
      .delay(1000, ioScheduler) // e.g. REST request
      .observeOn(scheduler)
      .doOnAfterNext { url ->
        println("Thread ${Thread.currentThread()}")
        goActionEnabled = true
        content = "Showing: $url"
      }
  }
})

fun main() = mainFrame("My Browser") {
  val view = SwingBrowserView()
  val presenter = BrowserPresenter(swingScheduler)
  contentPane = view.component
  presenter.start(view)
}

/**
 * Swing adaptation of the [BrowserView], can be also JavaFX, or HTML + JS or native iOS.
 */
class SwingBrowserView : BrowserView {
  private val urlText = textField(10)
  private val goAction = button("Go!")
  private val contentBox = label("") {
    horizontalAlignment = SwingConstants.CENTER
    verticalAlignment = SwingConstants.CENTER
    preferredSize = Dimension(300, 300)
  }

  override val urlEditEvents = urlText.textChanges
  override val goActions = goAction.actionEvents
  override var goActionEnabled
    get() = goAction.isEnabled
    set(value) { goAction.isEnabled = value }
  override var content: String
    get() = contentBox.text
    set(value) { contentBox.text = value }

  val component = borderPanel {
    layout.hgap = 4
    panel.border = emptyBorder(4)
    north = borderPanel {
      west = label("URL")
      center = urlText
      east = goAction
    }
    center = contentBox
  }

}
