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

import javax.swing.*

fun Button(
  text: String,
  block: (JButton.() -> Unit) = {}
): JButton = JButton(text).apply(block)

fun Label(
  text: String = "",
  block: (JLabel.() -> Unit) = {}
): JLabel = JLabel(text).apply(block)

fun TextField(
  text: String? = null,
  block: (JTextField.() -> Unit) = {}
): JTextField = JTextField(text).apply(block)

fun TextArea(
  text: String? = null,
  rows: Int = 0,
  columns: Int = 0,
  block: (JTextArea.() -> Unit) = {}
): JTextArea = JTextArea(text, rows, columns).apply(block)

fun RadioButton(
  label: String? = null,
  block: (JRadioButton.() -> Unit) = {}
): JRadioButton = JRadioButton(label).apply {
  block(this)
}

fun CheckBox(
  label: String? = null,
  block: (JCheckBox.() -> Unit) = {}
): JCheckBox = JCheckBox(label).apply {
  block(this)
}
