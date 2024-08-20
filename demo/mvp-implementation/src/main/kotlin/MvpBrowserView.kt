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

import com.xemantic.kotlin.swing.*

/**
 * Swing implementation of the [BrowserView], could be also JavaFX, or HTML + JS or native iOS.
 */
class SwingBrowserView : BrowserView {
  private val urlField = TextField()
  private val goAction = Button("Go!") {
    isEnabled = false
  }
  private val contentBox = TextArea()

  override val urlEdits = urlField.textChanges
  override val goActions = goAction.actionEvents.asActions
  override val urlActions = urlField.actionEvents.asActions
  override var goActionEnabled: Boolean
    get() = goAction.isEnabled
    set(value) { goAction.isEnabled = value }
  override var content: String
    get() = contentBox.text
    set(value) { contentBox.text = value }
  override var url: String
    get() = urlField.text
    set(value) { urlField.text = value }

  val swingComponent = BorderPanel {
    north {
      Border.empty(4) {
        BorderPanel {
          layout { gap = 4 }
          west { Label("URL") }
          center { urlField }
          east { goAction }
        }
      }
    }
    center {
      ScrollPane { contentBox }
    }
  }

}
