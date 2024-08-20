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
@file:Suppress("FunctionName")

package com.xemantic.kotlin.swing

import java.awt.*
import javax.swing.*

enum class BoxLayoutAxis(
  val code: Int
) {
  X(BoxLayout.X_AXIS),
  Y(BoxLayout.Y_AXIS),
  LINE(BoxLayout.LINE_AXIS),
  PAGE(BoxLayout.PAGE_AXIS)
}

fun BorderPanel(
  block: BorderPanelBuilder.() -> Unit
): JPanel = JPanel(
  BorderLayout()
).apply {
  block(BorderPanelBuilder(this))
}

fun FlowPanel(
  block: PanelBuilder<FlowLayout>.() -> Unit
): JPanel = JPanel(
  FlowLayout()
).apply {
  block(PanelBuilder(this))
}

fun BoxPanel(
  axis: BoxLayoutAxis,
  block: PanelBuilder<BoxLayout>.() -> Unit
): JPanel = JPanel().apply {
  layout = BoxLayout(this, axis.code)
  block(PanelBuilder(this))
}

fun ScrollPane(
  block: () -> Component
) = JScrollPane(block())

fun Grid(
  rows: Int,
  columns: Int,
  block: PanelBuilder<GridLayout>.() -> Unit
): JPanel = JPanel(
  GridLayout(rows, columns)
).apply {
  block(PanelBuilder(this))
}

class BorderPanelBuilder(
  val panel: JPanel
) {

  fun north(block: () -> JComponent) {
    panel.add(block(), BorderLayout.NORTH)
  }

  fun south(block: () -> JComponent) {
    panel.add(block(), BorderLayout.SOUTH)
  }

  fun east(block: () -> JComponent) {
    panel.add(block(), BorderLayout.EAST)
  }

  var west: JComponent
    get() = throw NotImplementedError()
    set(value) = panel.add(value, BorderLayout.WEST)

  fun west(block: () -> JComponent) {
    west = block()
  }

  var center: JComponent
    get() = throw NotImplementedError()
    set(value) = panel.add(value, BorderLayout.CENTER)
  fun center(block: () -> JComponent) {
    center = block()
  }

  val layout = panel.layout as BorderLayout

  fun layout(builder: BorderLayout.() -> Unit) {
    builder(panel.layout as BorderLayout)
  }

}

class PanelBuilder<L : LayoutManager>(
  val panel: JPanel
) {

  operator fun Component.unaryPlus() {
    panel.add(this)
  }

  @Suppress("UNCHECKED_CAST")
  val layout get() = panel.layout as L

  fun layout(builder: L.() -> Unit) {
    builder(layout)
  }

}

var BorderLayout.gap
  get() = vgap
  set(value) {
    vgap = value
    hgap = value
  }
