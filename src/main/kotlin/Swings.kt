package com.xemantic.kotlin.swing

import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import java.awt.*
import java.util.concurrent.TimeUnit
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

private val factory = DefaultJComponentFactory()

fun mainFrame(title: String, build: JFrame.() -> Unit) {
  SwingUtilities.invokeAndWait {
    val frame = JFrame(title)
    build(frame)
    frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    frame.pack()
    frame.isVisible = true
  }
}

fun borderPanel(build: BorderPanelBuilder.() -> Unit) : JPanel =
    factory.borderPanel(build)

fun flowPanel(build: PanelBuilder<FlowLayout>.() -> Unit) : JPanel =
    factory.flowPanel(build)

fun verticalPanel(build: PanelBuilder<BoxLayout>.() -> Unit) : JPanel =
    factory.verticalPanel(build)

fun grid(rows: Int, cols: Int, build: PanelBuilder<GridLayout>.() -> Unit): JPanel =
    factory.grid(rows, cols, build)

fun button(label: String, build: (JButton.() -> Unit)? = null) : JButton =
    factory.button(label, build)

fun label(label: String = "", build: (JLabel.() -> Unit)? = null) : JLabel =
    factory.label(label, build)

fun textField(columns: Int, build: (JTextField.() -> Unit)? = null) : JTextField =
    factory.textField(columns, build)

fun <T : JComponent> border(title: String, build: () -> T): T =
    factory.border(title, build)

class BorderPanelBuilder(val panel: JPanel) {
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

  fun flowPanel(build: PanelBuilder<FlowLayout>.() -> Unit): JPanel

  fun verticalPanel(build: PanelBuilder<BoxLayout>.() -> Unit): JPanel

  fun grid(rows: Int, cols: Int, build: PanelBuilder<GridLayout>.() -> Unit): JPanel

  fun button(label: String, build: (JButton.() -> Unit)? = null): JButton

  fun label(label: String = "", build: (JLabel.() -> Unit)? = null): JLabel

  fun textField(columns: Int, build: (JTextField.() -> Unit)? = null): JTextField

  fun radioButton(label: String, build: (JRadioButton.() -> Unit)? = null): JRadioButton

  fun <T : JComponent> border(title: String, build: () -> T): T

}

class PanelBuilder<L: LayoutManager>(val panel: JPanel) : JComponentFactory {

  override fun borderPanel(build: BorderPanelBuilder.() -> Unit): JPanel {
    val component = factory.borderPanel(build)
    panel.add(component)
    return component
  }

  override fun flowPanel(build: PanelBuilder<FlowLayout>.() -> Unit): JPanel {
    val component = factory.flowPanel(build)
    panel.add(component)
    return component
  }

  override fun verticalPanel(build: PanelBuilder<BoxLayout>.() -> Unit): JPanel {
    val component =  factory.verticalPanel(build)
    panel.add(component)
    return component
  }

  override fun grid(rows: Int, cols: Int, build: PanelBuilder<GridLayout>.() -> Unit): JPanel {
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

  val me = panel

  val layout = panel.layout as L

}

class DefaultJComponentFactory : JComponentFactory {

  override fun borderPanel(build: BorderPanelBuilder.() -> Unit) : JPanel {
    val panel = JPanel(BorderLayout())
    build(BorderPanelBuilder(panel))
    return panel
  }

  override fun flowPanel(build: PanelBuilder<FlowLayout>.() -> Unit) : JPanel {
    val panel = JPanel()
    build(PanelBuilder(panel))
    return panel
  }

  override fun verticalPanel(build: PanelBuilder<BoxLayout>.() -> Unit) : JPanel {
    val panel = JPanel()
    val layout = BoxLayout(panel, BoxLayout.Y_AXIS)
    panel.layout = layout
    build(PanelBuilder(panel))
    return panel
  }

  override fun grid(rows: Int, cols: Int, build: PanelBuilder<GridLayout>.() -> Unit): JPanel {
    val panel = JPanel(GridLayout(rows, cols))
    build(PanelBuilder(panel))
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

@FunctionalInterface
interface TextChangeListener : DocumentListener {

  fun onChange(text: String)

  override fun insertUpdate(e: DocumentEvent) { fireChange(e) }

  override fun removeUpdate(e: DocumentEvent) { fireChange(e) }

  override fun changedUpdate(e: DocumentEvent) { fireChange(e) }

  private fun fireChange(e: DocumentEvent) {
    onChange(e.document.getText(0, e.document.length))
  }

}

fun JTextField.observeTextChange(observer: Observer<String>) {
  document.addDocumentListener(object : DocumentListener {
    override fun insertUpdate(e: DocumentEvent) { fireChange(e) }
    override fun removeUpdate(e: DocumentEvent) { fireChange(e) }
    override fun changedUpdate(e: DocumentEvent) { fireChange(e) }
    private fun fireChange(e: DocumentEvent) {
      observer.onNext(e.document.getText(0, e.document.length))
    }
  })
}

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
