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

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.awt.Component
import java.awt.event.*
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.Document
import javax.swing.text.JTextComponent

/**
 * Represents and action, like click or touch event, without
 * any additional attributes.
 */
class Action internal constructor()

/**
 * An action instance.
 */
val action = Action()

val Flow<ActionEvent>.asActions get() = map { action }

val Component.mouseEvents: Flow<MouseEvent> get() = callbackFlow {
  val listener = object : MouseListener {
    override fun mouseClicked(e: MouseEvent) { trySend(e) }
    override fun mouseEntered(e: MouseEvent) { trySend(e) }
    override fun mouseExited(e: MouseEvent) { trySend(e) }
    override fun mousePressed(e: MouseEvent) { trySend(e) }
    override fun mouseReleased(e: MouseEvent) { trySend(e) }
  }
  val motionListener = object : MouseMotionListener {
    override fun mouseDragged(e: MouseEvent) { trySend(e) }
    override fun mouseMoved(e: MouseEvent) { trySend(e) }
  }
  addMouseListener(listener)
  addMouseMotionListener(motionListener)
  awaitClose {
    removeMouseListener(listener)
    removeMouseMotionListener(motionListener)
  }
}

val Component.mouseMoves: Flow<MouseEvent> get() =
  mouseEvents.filter { it.id == MouseEvent.MOUSE_MOVED }

val Component.mouseClicks: Flow<MouseEvent> get() =
  mouseEvents.filter { it.id == MouseEvent.MOUSE_CLICKED }

val Component.mouseDrags: Flow<MouseEvent> get() =
  mouseEvents.filter { it.id == MouseEvent.MOUSE_DRAGGED }

val Component.mousePresses: Flow<MouseEvent> get() =
  mouseEvents.filter { it.id == MouseEvent.MOUSE_PRESSED }

val Component.mouseReleases: Flow<MouseEvent> get() =
  mouseEvents.filter { it.id == MouseEvent.MOUSE_RELEASED }

val Component.mouseEnters: Flow<MouseEvent> get() =
  mouseEvents.filter { it.id == MouseEvent.MOUSE_ENTERED }

val Component.mouseExits: Flow<MouseEvent> get() =
  mouseEvents.filter { it.id == MouseEvent.MOUSE_EXITED }

val Component.focusEvents: Flow<FocusEvent> get() = callbackFlow {
  val listener = object : FocusListener {
    override fun focusGained(e: FocusEvent) { trySend(e) }
    override fun focusLost(e: FocusEvent) { trySend(e) }
  }
  addFocusListener(listener)
  awaitClose {
    removeFocusListener(listener)
  }
}

val Component.focusGains: Flow<FocusEvent> get() =
  focusEvents.filter { it.id == FocusEvent.FOCUS_GAINED }

val Component.focusLosses: Flow<FocusEvent> get() =
  focusEvents.filter { it.id == FocusEvent.FOCUS_LOST }

val JTextField.actionEvents: Flow<ActionEvent> get() = callbackFlow {
  val listener = ActionListener { e -> trySend(e) }
  addActionListener(listener)
  awaitClose { removeActionListener(listener) }
}

val AbstractButton.actionEvents: Flow<ActionEvent> get() = callbackFlow {
  val listener = ActionListener { e -> trySend(e) }
  addActionListener(listener)
  awaitClose { removeActionListener(listener) }
}

val Document.documentChanges: Flow<DocumentEvent> get() = callbackFlow {
  val listener = object : DocumentListener {
    override fun insertUpdate(e: DocumentEvent) { trySend(e) }
    override fun removeUpdate(e: DocumentEvent) { trySend(e) }
    override fun changedUpdate(e: DocumentEvent) { trySend(e) }
  }
  addDocumentListener(listener)
  awaitClose { removeDocumentListener(listener) }
}

val JTextComponent.documentChanges: Flow<DocumentEvent>
  get() = document.documentChanges

val JTextComponent.textChanges: Flow<String>
  get() = documentChanges.map { text }
