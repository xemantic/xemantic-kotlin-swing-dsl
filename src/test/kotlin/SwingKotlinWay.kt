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
import com.badoo.reaktive.scheduler.ioScheduler
import java.awt.Dimension
import javax.swing.SwingConstants

fun main() = mainFrame("My Browser") {
  val contentBox = label("") {
    horizontalAlignment = SwingConstants.CENTER
    verticalAlignment = SwingConstants.CENTER
    preferredSize = Dimension(300, 300)
  }
  val urlBox = textField(10)
  val goAction = button("Go!") {
    isEnabled = false
    actionEvents
      .withLatestFrom(urlBox.textChanges) { _, url: String -> url }
      .doOnAfterNext { url ->
        isEnabled = false
        contentBox.text = "Loading: $url"
      }
      .delay(1000, ioScheduler) // e.g. REST request
      .observeOn(swingScheduler)
      .subscribe { url ->
        isEnabled = true
        contentBox.text = "Ready: $url"
      }
  }
  urlBox.textChanges.subscribe { url ->
    goAction.isEnabled = url.isNotBlank()
    contentBox.text = "Will load: $url"
  }
  contentPane = borderPanel {
    layout.hgap = 4
    panel.border = emptyBorder(4)
    north = borderPanel {
      west = label("URL")
      center = urlBox
      east = goAction
    }
    center = contentBox
  }
}
