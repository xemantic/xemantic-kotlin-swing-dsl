/*
 * xemantic-kotlin-swing-dsl - swing DSL for kotlin
 *
 * Copyright (C) 2020  Kazimierz Pogoda
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.xemantic.kotlin.swing

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.awt.Dimension
import java.util.concurrent.TimeUnit
import javax.swing.SwingConstants

fun main() = mainFrame("SwingScheduler example") {
  val ticks = PublishSubject.create<Long>()
  contentPane = label{
    ticks.observeOn(swingScheduler)
        .subscribe { tick -> text = tick.toString() }
    preferredSize = Dimension(200, 200)
    horizontalAlignment = SwingConstants.CENTER
  }
  Observable.interval(1, TimeUnit.SECONDS)
      .subscribe { tick -> ticks.onNext(tick) }
}
