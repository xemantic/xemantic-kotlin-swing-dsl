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

import com.badoo.reaktive.observable.observableInterval
import com.badoo.reaktive.observable.subscribe
import java.awt.Dimension
import javax.swing.SwingConstants

fun main() = mainFrame("swingScheduler example") {
  contentPane = label{
    observableInterval(1000, swingScheduler)
      .subscribe { tick ->
        text = tick.toString()
      }
    preferredSize = Dimension(100, 100)
    horizontalAlignment = SwingConstants.CENTER
  }
}
