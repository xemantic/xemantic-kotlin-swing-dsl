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

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import java.awt.Container
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.WindowConstants

/**
 * Creates new main [JFrame] with given title. The frame will be
 * constructed in the new [MainScope] associated with this frame. This
 * coroutine scope will be cancelled when the window is closed.
 */
fun MainWindow(
  title: String,
  block: SwingScope.(frame: JFrame) -> Container
) {
  val scope = MainScope()
  val swingScope = SwingScope(scope)
  scope.launch(CoroutineName("MainWindow")) {
    JFrame(title).apply {
      contentPane = block(swingScope, this)
      pack()
      defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
      onClosing {
        MainScope().launch {
          scope.coroutineContext.job.cancelAndJoin()
          System.exit(0)
        }
      }
      isVisible = true
    }
  }
}

class SwingScope(
  val scope: CoroutineScope
) {

  inline fun <reified T> Flow<T>.listen(
    name: String? = null,
    collector: FlowCollector<T>
  ) {
    val listenerName = if (name != null) "-$name" else ""
    scope.launch(
      CoroutineName(
        "listen$listenerName[${T::class.java.name}]"
      )
    ) {
      collect(collector)
    }
  }

  fun frame(
    title: String,
    block: suspend SwingScope.(frame: JFrame) -> Container
  ): JFrame {
    val frame = JFrame(title)
    scope.launch(SupervisorJob(scope.coroutineContext.job)) {
      val frameScope = this
      val frameSwingScope = SwingScope(frameScope)
      frame.apply {
        contentPane = block(frameSwingScope, this)
        pack()
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        onClosing {
          scope.launch {
            frameScope.coroutineContext.job.cancelAndJoin()
            dispose()
          }
        }
        isVisible = true
      }
    }
    return frame
  }

  fun JFrame.dialog(
    title: String,
    modal: Boolean = false,
    block: SwingScope.(dialog: JDialog) -> Container,
  ): JDialog {
    val dialog = JDialog(this@dialog, title, modal)
    scope.launch(SupervisorJob(scope.coroutineContext.job)) {
      val dialogScope = this
      val dialogSwingScope = SwingScope(dialogScope)
      dialog.apply {
        contentPane = block(dialogSwingScope, this)
        pack()
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        onClosing {
          scope.launch {
            dialogScope.coroutineContext.job.cancelAndJoin()
            dispose()
          }
        }
        isVisible = true
      }
    }
    return dialog
  }

}

fun CoroutineScope.swingScope(
  block: SwingScope.() -> Unit
): SwingScope {
  val swingScope = SwingScope(this)
  block(swingScope)
  return swingScope
}

private fun JFrame.onClosing(block: () -> Unit) {
  addWindowListener(object : WindowAdapter() {
    override fun windowClosing(e: WindowEvent) {
      removeWindowListener(this)
      block()
    }
  })
}

private fun JDialog.onClosing(block: () -> Unit) {
  addWindowListener(object : WindowAdapter() {
    override fun windowClosing(e: WindowEvent) {
      removeWindowListener(this)
      block()
    }
  })
}
