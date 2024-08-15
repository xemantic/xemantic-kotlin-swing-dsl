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
package com.xemantic.kotlin.swing

import javax.swing.*

class Border {

  companion object {

    fun <T : JComponent> title(
      title: String,
      block: () -> T
    ): T {
      val component = block()
      component.border = BorderFactory.createTitledBorder(title)
      return component
    }

    fun <T : JComponent> empty(
      width: Int,
      block: () -> T
    ): T  = empty(
      top = width,
      left = width,
      bottom = width,
      right = width,
      block
    )

    fun <T : JComponent> empty(
      top: Int,
      left: Int,
      bottom: Int,
      right: Int,
      block: () -> T
    ): T {
      val component = block()
      component.border = BorderFactory.createEmptyBorder(top, left, bottom, right)
      return component
    }

  }

}
