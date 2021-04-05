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

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.subscribe

/**
 * A presenter base.
 */
abstract class Presenter<V>(
  builder: Presenter<V>.Builder.() -> Unit
) {

  private val starters = mutableListOf<V.() -> Observable<*>>()

  inner class Builder {
    fun observe(block: V.() -> Observable<*>) {
      starters.add(block)
    }
  }

  init {
    builder(Builder())
  }

  private val observables = mutableListOf<Observable<*>>()

  private lateinit var subscriptions: List<Disposable>

  fun start(view: V) {
    subscriptions = starters
      .map { it(view) }
      .map { it.subscribe() }
  }

  fun stop() {
    subscriptions.forEach { it.dispose() }
  }

}
