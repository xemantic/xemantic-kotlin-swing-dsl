package com.xemantic.kotlin.swing

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.util.concurrent.TimeUnit
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

private val factory = DefaultJComponentFactory()

fun mainFrame(title: String, build: JFrame.() -> Unit) = SwingUtilities.invokeAndWait {
  val frame = JFrame(title)
  build(frame)
  frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
  frame.pack()
  frame.isVisible = true
}

fun borderPanel(build: BorderPanelBuilder.() -> Unit) : JPanel =
    factory.borderPanel(build)

fun flowPanel(build: DefaultPanelBuilder<FlowLayout>.() -> Unit) : JPanel =
    factory.flowPanel(build)

fun verticalPanel(build: DefaultPanelBuilder<BoxLayout>.() -> Unit) : JPanel =
    factory.verticalPanel(build)

fun grid(rows: Int, cols: Int, build: DefaultPanelBuilder<GridLayout>.() -> Unit): JPanel =
    factory.grid(rows, cols, build)

fun button(label: String, build: (JButton.() -> Unit)? = null) : JButton =
    factory.button(label, build)

fun label(label: String = "", build: (JLabel.() -> Unit)? = null) : JLabel =
    factory.label(label, build)

fun textField(columns: Int, build: (JTextField.() -> Unit)? = null) : JTextField =
    factory.textField(columns, build)

fun <T : JComponent> border(title: String, build: () -> T): T =
    factory.border(title, build)

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

  fun radioButton(label: String, build: (JRadioButton.() -> Unit)? = null): JRadioButton

  fun <T : JComponent> border(title: String, build: () -> T): T

}

class DefaultPanelBuilder<L: LayoutManager>(override val panel: JPanel) : PanelBuilder, JComponentFactory {

  override fun borderPanel(build: BorderPanelBuilder.() -> Unit): JPanel {
    val component = factory.borderPanel(build)
    panel.add(component)
    return component
  }

  override fun flowPanel(build: DefaultPanelBuilder<FlowLayout>.() -> Unit): JPanel {
    val component = factory.flowPanel(build)
    panel.add(component)
    return component
  }

  override fun verticalPanel(build: DefaultPanelBuilder<BoxLayout>.() -> Unit): JPanel {
    val component =  factory.verticalPanel(build)
    panel.add(component)
    return component
  }

  override fun grid(rows: Int, cols: Int, build: DefaultPanelBuilder<GridLayout>.() -> Unit): JPanel {
    val component =  factory.grid(rows, cols, build)
    panel.add(component)
    return component
  }

  override fun button(label: String, build: (JButton.() -> Unit)?): JButton {
    val component = factory.button(label, build)
    panel.add(component)
    return component
  }

  override fun label(label: String, build: (JLabel.() -> Unit)?): JLabel {
    val component = factory.label(label, build)
    panel.add(component)
    return component
  }

  override fun textField(columns: Int, build: (JTextField.() -> Unit)?): JTextField {
    val component = factory.textField(columns, build)
    panel.add(component)
    return component
  }

  override fun radioButton(label: String, build: (JRadioButton.() -> Unit)?): JRadioButton {
    val component = factory.radioButton(label, build)
    panel.add(component)
    return component
  }

  override fun <T : JComponent> border(title: String, build: () -> T): T {
    val component = factory.border(title, build)
    panel.add(component)
    return component
  }

  fun add(component: Component) {
    panel.add(component)
  }

  @Suppress("UNCHECKED_CAST")
  val layout = panel.layout as L

}

class DefaultJComponentFactory : JComponentFactory {

  override fun borderPanel(build: BorderPanelBuilder.() -> Unit) : JPanel {
    val panel = JPanel(BorderLayout())
    build(BorderPanelBuilder(panel))
    return panel
  }

  override fun flowPanel(build: DefaultPanelBuilder<FlowLayout>.() -> Unit) : JPanel {
    val panel = JPanel()
    build(DefaultPanelBuilder(panel))
    return panel
  }

  override fun verticalPanel(build: DefaultPanelBuilder<BoxLayout>.() -> Unit) : JPanel {
    val panel = JPanel()
    val layout = BoxLayout(panel, BoxLayout.Y_AXIS)
    panel.layout = layout
    build(DefaultPanelBuilder(panel))
    return panel
  }

  override fun grid(rows: Int, cols: Int, build: DefaultPanelBuilder<GridLayout>.() -> Unit): JPanel {
    val panel = JPanel(GridLayout(rows, cols))
    build(DefaultPanelBuilder(panel))
    return panel
  }

  override fun button(label: String, build: (JButton.() -> Unit)?) : JButton {
    val component = JButton(label)
    if (build != null) build(component)
    return component
  }

  override fun label(label: String, build: (JLabel.() -> Unit)?) : JLabel {
    val component = JLabel(label)
    if (build != null) build(component)
    return component
  }

  override fun textField(columns: Int, build: (JTextField.() -> Unit)?) : JTextField {
    val component = JTextField(columns)
    if (build != null) build(component)
    return component
  }

  override fun radioButton(label: String, build: (JRadioButton.() -> Unit)?): JRadioButton {
    val component = JRadioButton(label)
    if (build != null) build(component)
    return component
  }

  override fun <T : JComponent> border(title: String, build: () -> T): T {
    val component = build()
    component.border = BorderFactory.createTitledBorder(title)
    return component
  }

}

// TODO all these properties should be probably cached

val JButton.actionEvents: Observable<ActionEvent>
  get() = Observable.create { emitter ->
    val listener = ActionListener { e -> emitter.onNext(e) }
    addActionListener(listener)
    emitter.setCancellable { removeActionListener(listener) }
  }

val JButton.mouseEvents: Observable<MouseEvent>
  get() = Observable.create { emitter ->
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
  get() = Observable.create { emitter ->
    val listener = ActionListener { e -> emitter.onNext(e) }
    addActionListener(listener)
    emitter.setCancellable { removeActionListener(listener) }
  }

val JRadioButton.actionEvents: Observable<ActionEvent>
  get() = Observable.create { emitter ->
    val listener = ActionListener { e -> emitter.onNext(e) }
    addActionListener(listener)
    emitter.setCancellable { removeActionListener(listener) }
  }

val JTextField.documentChanges: Observable<DocumentEvent>
  get() = Observable.create { emitter ->
    val listener = object : DocumentListener {
      override fun insertUpdate(e: DocumentEvent) { fireChange(e) }
      override fun removeUpdate(e: DocumentEvent) { fireChange(e) }
      override fun changedUpdate(e: DocumentEvent) { fireChange(e) }
      private fun fireChange(e: DocumentEvent) { emitter.onNext(e) }
    }
    document.addDocumentListener(listener)
    emitter.setCancellable { document.removeDocumentListener(listener) }
  }

val JTextField.textChanges: Observable<String>
  get() = documentChanges.map { text }



val swingScheduler = SwingScheduler()

class SwingScheduler : Scheduler() {

  private val worker: Worker = SwingWorker()
  private val disposed: Disposable = Disposable.empty()

  init { disposed.dispose() }

  override fun scheduleDirect(run: Runnable): Disposable {
    SwingUtilities.invokeLater(run)
    return disposed
  }

  override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
    throw UnsupportedOperationException("This scheduler doesn't support delayed execution")
  }

  override fun schedulePeriodicallyDirect(run: Runnable, initialDelay: Long, period: Long, unit: TimeUnit): Disposable {
    throw UnsupportedOperationException("This scheduler doesn't support periodic execution")
  }

  override fun createWorker(): Worker {
    return worker
  }

  inner class SwingWorker : Worker() {
    override fun dispose() {
      // This worker is always stateless and won't track tasks
    }

    override fun isDisposed(): Boolean {
      return false // dispose() has no effect
    }

    override fun schedule(run: Runnable): Disposable {
      SwingUtilities.invokeLater(run)
      return disposed
    }

    override fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
      throw UnsupportedOperationException("This scheduler doesn't support delayed execution")
    }

    override fun schedulePeriodically(run: Runnable, initialDelay: Long, period: Long, unit: TimeUnit): Disposable {
      throw UnsupportedOperationException("This scheduler doesn't support periodic execution")
    }

  }

}
