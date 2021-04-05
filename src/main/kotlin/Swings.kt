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

import com.badoo.reaktive.base.setCancellable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.observable
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.scheduler.computationScheduler
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.*
import javax.swing.border.Border
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.JTextComponent

private val factory = DefaultJComponentFactory()

fun mainFrame(title: String, build: JFrame.() -> Unit) = SwingUtilities.invokeAndWait {
  val frame = JFrame(title)
  build(frame)
  frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
  frame.pack()
  frame.isVisible = true
}

fun borderPanel(build: BorderPanelBuilder.() -> Unit): JPanel =
  factory.borderPanel(build)

fun flowPanel(build: DefaultPanelBuilder<FlowLayout>.() -> Unit): JPanel =
  factory.flowPanel(build)

fun verticalPanel(build: DefaultPanelBuilder<BoxLayout>.() -> Unit): JPanel =
  factory.verticalPanel(build)

fun grid(rows: Int, cols: Int, build: DefaultPanelBuilder<GridLayout>.() -> Unit): JPanel =
  factory.grid(rows, cols, build)

fun button(label: String, build: (JButton.() -> Unit)? = null): JButton =
  factory.button(label, build)

fun label(label: String = "", build: (JLabel.() -> Unit)? = null): JLabel =
  factory.label(label, build)

fun textField(columns: Int, build: (JTextField.() -> Unit)? = null): JTextField =
  factory.textField(columns, build)

fun textArea(
  text: String? = null, rows: Int = 0, columns: Int = 0, build: (JTextArea.() -> Unit)? = null
): JTextArea =
  factory.textArea(text, rows, columns, build)

fun <T : JComponent> border(title: String, build: () -> T): T =
  factory.border(title, build)

fun emptyBorder(width: Int): Border = emptyBorder(width, width, width, width)

fun emptyBorder(top: Int, left: Int, bottom: Int, right: Int): Border =
  BorderFactory.createEmptyBorder(top, left, bottom, right)

interface PanelBuilder {
  val panel: JPanel
}

class BorderPanelBuilder(override val panel: JPanel) : PanelBuilder {
  var north: JComponent
    get() = throw NotImplementedError()
    set(value) = panel.add(value, BorderLayout.NORTH)
  var south: JComponent
    get() = throw NotImplementedError()
    set(value) = panel.add(value, BorderLayout.SOUTH)
  var east: JComponent
    get() = throw NotImplementedError()
    set(value) = panel.add(value, BorderLayout.EAST)
  var west: JComponent
    get() = throw NotImplementedError()
    set(value) = panel.add(value, BorderLayout.WEST)
  var center: JComponent
    get() = throw NotImplementedError()
    set(value) = panel.add(value, BorderLayout.CENTER)
  val layout = panel.layout as BorderLayout
}

interface JComponentFactory {

  fun borderPanel(build: BorderPanelBuilder.() -> Unit): JPanel

  fun flowPanel(build: DefaultPanelBuilder<FlowLayout>.() -> Unit): JPanel

  fun verticalPanel(build: DefaultPanelBuilder<BoxLayout>.() -> Unit): JPanel

  fun grid(rows: Int, cols: Int, build: DefaultPanelBuilder<GridLayout>.() -> Unit): JPanel

  fun button(label: String, build: (JButton.() -> Unit)? = null): JButton

  fun label(label: String = "", build: (JLabel.() -> Unit)? = null): JLabel

  fun textField(columns: Int, build: (JTextField.() -> Unit)? = null): JTextField

  fun textArea(
    text: String? = null, rows: Int = 0, columns: Int = 0, build: (JTextArea.() -> Unit)? = null
  ): JTextArea

  fun radioButton(label: String, build: (JRadioButton.() -> Unit)? = null): JRadioButton

  fun <T : JComponent> border(title: String, build: () -> T): T

}

class DefaultPanelBuilder<L : LayoutManager>(override val panel: JPanel) :
  PanelBuilder, JComponentFactory {

  override fun borderPanel(build: BorderPanelBuilder.() -> Unit): JPanel =
    add(factory.borderPanel(build))

  override fun flowPanel(build: DefaultPanelBuilder<FlowLayout>.() -> Unit): JPanel =
    add(factory.flowPanel(build))

  override fun verticalPanel(build: DefaultPanelBuilder<BoxLayout>.() -> Unit): JPanel =
    add(factory.verticalPanel(build))

  override fun grid(
    rows: Int, cols: Int, build: DefaultPanelBuilder<GridLayout>.() -> Unit
  ): JPanel =
    add(factory.grid(rows, cols, build))

  override fun button(label: String, build: (JButton.() -> Unit)?): JButton =
    add(factory.button(label, build))

  override fun label(label: String, build: (JLabel.() -> Unit)?): JLabel =
    add(factory.label(label, build))

  override fun textField(columns: Int, build: (JTextField.() -> Unit)?): JTextField =
    add(factory.textField(columns, build))

  override fun textArea(
    text: String?, rows: Int, columns: Int, build: (JTextArea.() -> Unit)?
  ): JTextArea =
    add(factory.textArea(text, rows, columns, build))

  override fun radioButton(label: String, build: (JRadioButton.() -> Unit)?): JRadioButton =
    add(factory.radioButton(label, build))

  override fun <T : JComponent> border(title: String, build: () -> T): T =
    add(factory.border(title, build))

  fun <T : Component> add(component: T): T {
    panel.add(component)
    return component
  }

  @Suppress("UNCHECKED_CAST")
  val layout = panel.layout as L

}

class DefaultJComponentFactory : JComponentFactory {

  override fun borderPanel(build: BorderPanelBuilder.() -> Unit): JPanel {
    val panel = JPanel(BorderLayout())
    build(BorderPanelBuilder(panel))
    return panel
  }

  override fun flowPanel(build: DefaultPanelBuilder<FlowLayout>.() -> Unit): JPanel {
    val panel = JPanel()
    build(DefaultPanelBuilder(panel))
    return panel
  }

  override fun verticalPanel(build: DefaultPanelBuilder<BoxLayout>.() -> Unit): JPanel {
    val panel = JPanel()
    val layout = BoxLayout(panel, BoxLayout.Y_AXIS)
    panel.layout = layout
    build(DefaultPanelBuilder(panel))
    return panel
  }

  override fun grid(
    rows: Int, cols: Int, build: DefaultPanelBuilder<GridLayout>.() -> Unit
  ): JPanel {
    val panel = JPanel(GridLayout(rows, cols))
    build(DefaultPanelBuilder(panel))
    return panel
  }

  override fun button(label: String, build: (JButton.() -> Unit)?): JButton =
    make(JButton(label), build)

  override fun label(label: String, build: (JLabel.() -> Unit)?): JLabel =
    make(JLabel(label), build)

  override fun textField(columns: Int, build: (JTextField.() -> Unit)?): JTextField =
    make(JTextField(columns), build)

  override fun textArea(
    text: String?, rows: Int, columns: Int, build: (JTextArea.() -> Unit)?
  ): JTextArea =
    make(JTextArea(text, rows, columns), build)

  override fun radioButton(label: String, build: (JRadioButton.() -> Unit)?): JRadioButton =
    make(JRadioButton(label), build)

  override fun <T : JComponent> border(title: String, build: () -> T): T {
    val component = build()
    component.border = BorderFactory.createTitledBorder(title)
    return component
  }

  private fun <T : JComponent> make(component: T, build: ((T) -> Unit)?): T {
    if (build != null) build(component)
    return component
  }

}

// TODO all these properties should be probably cached

val JButton.actionEvents: Observable<ActionEvent>
  get() = observable { emitter ->
    val listener = ActionListener { e -> emitter.onNext(e) }
    addActionListener(listener)
    emitter.setCancellable { removeActionListener(listener) }
  }

val JButton.mouseEvents: Observable<MouseEvent>
  get() = observable { emitter ->
    val listener = object : MouseListener {
      override fun mouseClicked(e: MouseEvent) {
        fireChange(e)
      }

      override fun mouseEntered(e: MouseEvent) {
        fireChange(e)
      }

      override fun mouseExited(e: MouseEvent) {
        fireChange(e)
      }

      override fun mousePressed(e: MouseEvent) {
        fireChange(e)
      }

      override fun mouseReleased(e: MouseEvent) {
        fireChange(e)
      }

      private fun fireChange(e: MouseEvent) {
        emitter.onNext(e)
      }
    }
    addMouseListener(listener)
    emitter.setCancellable { removeMouseListener(listener) }
  }

val JTextField.actionEvents: Observable<ActionEvent>
  get() = observable { emitter ->
    val listener = ActionListener { e -> emitter.onNext(e) }
    addActionListener(listener)
    emitter.setCancellable { removeActionListener(listener) }
  }

val JRadioButton.actionEvents: Observable<ActionEvent>
  get() = observable { emitter ->
    val listener = ActionListener { e -> emitter.onNext(e) }
    addActionListener(listener)
    emitter.setCancellable { removeActionListener(listener) }
  }

val JTextComponent.documentChanges: Observable<DocumentEvent>
  get() = observable { emitter ->
    val listener = object : DocumentListener {
      override fun insertUpdate(e: DocumentEvent) {
        fireChange(e)
      }

      override fun removeUpdate(e: DocumentEvent) {
        fireChange(e)
      }

      override fun changedUpdate(e: DocumentEvent) {
        fireChange(e)
      }

      private fun fireChange(e: DocumentEvent) {
        emitter.onNext(e)
      }
    }
    document.addDocumentListener(listener)
    emitter.setCancellable { document.removeDocumentListener(listener) }
  }

val JTextComponent.textChanges: Observable<String>
  get() = documentChanges.map { text }


val swingScheduler = object : Scheduler {

  private val executor = object : Scheduler.Executor {

    private val waiter = computationScheduler.newExecutor()

    override fun submit(delayMillis: Long, task: () -> Unit) {
      waiter.submit(delayMillis) {
        SwingUtilities.invokeLater(task)
      }
    }

    override fun submitRepeating(
      startDelayMillis: Long,
      periodMillis: Long,
      task: () -> Unit
    ) {
      waiter.submitRepeating(startDelayMillis, periodMillis) {
        SwingUtilities.invokeLater(task)
      }
    }

    override val isDisposed: Boolean = waiter.isDisposed

    override fun cancel() {
      waiter.cancel()
    }

    override fun dispose() {
      waiter.dispose()
    }

  }

  override fun newExecutor(): Scheduler.Executor = executor

  override fun destroy() {
    /* does nothing for swing */
  }

}
