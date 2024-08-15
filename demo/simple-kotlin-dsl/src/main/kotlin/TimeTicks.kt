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

package com.xemantic.kotlin.swing.demo

import com.xemantic.kotlin.swing.Label
import com.xemantic.kotlin.swing.MainWindow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.awt.Dimension
import javax.swing.SwingConstants
import kotlin.time.Duration.Companion.seconds

fun main() = MainWindow("Time Ticks") {
  Label {
    preferredSize = Dimension(100, 100)
    horizontalAlignment = SwingConstants.CENTER
    flow {
      var count = 0
      while (true) {
        emit(count++)
        delay(1.seconds) // delay is suspending the coroutine
        // so we are not blocking Swing event dispatcher thread
      }
    }.listen {
      text = "$it"
    }
  }
}
