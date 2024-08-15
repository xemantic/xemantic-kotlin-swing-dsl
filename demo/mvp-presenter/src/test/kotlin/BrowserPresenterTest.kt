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
package com.xemantic.kotlin.swing.demo.mvp

import com.xemantic.kotlin.swing.Action
import com.xemantic.kotlin.swing.action
import com.xemantic.kotlin.swing.dsl.test.runSwingTest
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.beBlank
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
class BrowserPresenterTest {

  @Test
  fun `content should be blank and go action should be disabled initially`() = runSwingTest {
    BrowserPresenter(view, this, internet)
    advanceUntilIdle()

    // then
    view.apply {
      content should beBlank()
      goActionEnabled shouldBe false
    }
  }

  @Test
  fun `go action should be enabled after typing some characters`() = runSwingTest {
    // given
    val presenter = BrowserPresenter(view, this, internet)
    advanceUntilIdle()

    // when
    view.urlEdits.emit("foo")
    advanceUntilIdle()

    // then
    presenter.apply {
      loading shouldBe false
      url shouldBe "foo"
    }
    view.apply {
      content should beBlank()
      goActionEnabled shouldBe true
    }
  }

  @Test
  fun `go action should download and open the URL`() = runSwingTest {
    // given
    val presenter = BrowserPresenter(view, this, internet)
    advanceUntilIdle()
    view.urlEdits.emit("https://example.com")

    // when
    view.goActions.emit(action)
    advanceUntilIdle()

    // then
    presenter.apply {
      loading shouldBe false
      url shouldBe "https://example.com"
    }
    view.apply {
      content shouldBe "pong: https://example.com"
      goActionEnabled shouldBe true
    }
  }

  @Test
  fun `url action should download and open the URL`() = runSwingTest {
    // given
    val presenter = BrowserPresenter(view, this, internet)
    advanceUntilIdle()
    view.urlEdits.emit("https://example.com")

    // when
    view.urlActions.emit(action)
    advanceUntilIdle()

    // then
    presenter.apply {
      loading shouldBe false
      url shouldBe "https://example.com"
    }
    view.apply {
      content shouldBe "pong: https://example.com"
      goActionEnabled shouldBe true
    }
  }

  @Test
  fun `open(url) should download and open the URL`() = runSwingTest {
    // given
    val presenter = BrowserPresenter(view, this, internet)
    advanceUntilIdle()

    // when
    presenter.open("https://example.com")
    advanceUntilIdle()

    // then
    presenter.apply {
      loading shouldBe false
      url shouldBe "https://example.com"
    }
    view.apply {
      content shouldBe "pong: https://example.com"
      goActionEnabled shouldBe true
      url shouldBe  "https://example.com"
    }
  }

  @Test
  fun `should be in loading state until content is downloaded`() = runSwingTest {
    // given
    val internet = object : Internet {
      val completable = CompletableDeferred<Unit>()
      override suspend fun download(url: String): String {
        completable.await()
        return "pong: $url"
      }
      fun downloaded() {
        completable.complete(Unit)
      }
    }
    val presenter = BrowserPresenter(view, this, internet)
    advanceUntilIdle()
    view.urlEdits.emit("https://example.com")

    // when
    view.urlActions.emit(action)
    advanceUntilIdle()

    // then
    presenter.loading shouldBe true
    view.goActionEnabled shouldBe false

    // when
    internet.downloaded()
    advanceUntilIdle()

    // then
    presenter.apply {
      loading shouldBe false
      url shouldBe "https://example.com"
    }
    view.apply {
      content shouldBe "pong: https://example.com"
      goActionEnabled shouldBe true
    }
  }

  @Test
  fun `should display error if exception is thrown`() = runSwingTest {
    // given
    val internet = object : Internet {
      override suspend fun download(url: String): String {
        throw IllegalArgumentException("Invalid URL: $url")
      }
    }
    BrowserPresenter(view, this, internet)
    advanceUntilIdle()
    view.urlEdits.emit("foo")

    // when
    view.urlActions.emit(action)
    advanceUntilIdle()

    // then
    view.content shouldBe "java.lang.IllegalArgumentException: Invalid URL: foo"
  }

  private val view = object : BrowserView {
    override val urlEdits: MutableSharedFlow<String> = MutableSharedFlow()
    override val goActions: MutableSharedFlow<Action> = MutableSharedFlow()
    override val urlActions: MutableSharedFlow<Action> = MutableSharedFlow()
    override var goActionEnabled: Boolean = false
    override var content: String = ""
    override var url: String = ""
  }

  private val internet = object : Internet {
    override suspend fun download(url: String): String = "pong: $url"
  }

}
