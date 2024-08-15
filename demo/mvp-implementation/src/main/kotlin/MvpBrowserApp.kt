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
import kotlinx.coroutines.launch
import java.awt.Dimension

fun main(vararg args: String) = MainWindow("My Browser") { window ->
  window.preferredSize = Dimension(300, 300)
  val internet = DefaultInternet()
  val view = SwingBrowserView()
  val presenter = BrowserPresenter(view, scope, internet)
  if (args.isNotEmpty()) {
    val url = args[0]
    scope.launch {
      presenter.open(url)
    }
  }
  view.swingComponent
}
