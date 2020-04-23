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

import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.subjects.PublishSubject
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.BorderFactory
import javax.swing.SwingConstants

fun main() = mainFrame("My Browser") {
  val newUrlEvents = PublishSubject.create<String>()
  val urlEditEvents = PublishSubject.create<String>()
  contentPane = borderPanel {
    layout.hgap = 4
    panel.border = BorderFactory.createEmptyBorder(4, 4, 4, 4)
    north = borderPanel {
      west = label("URL")
      center = textField(10) {
        textChanges.subscribe(urlEditEvents)
        actionEvents.map { text }.subscribe(newUrlEvents)
      }
      east = button("Go!") {
        actionEvents
            .withLatestFrom(
                urlEditEvents,
                BiFunction { _: ActionEvent, url: String -> url }
            )
            .subscribe(newUrlEvents)
        urlEditEvents.subscribe { url -> isEnabled = url.isNotBlank() }
      }
    }
    center = label {
      horizontalAlignment = SwingConstants.CENTER
      verticalAlignment = SwingConstants.CENTER
      preferredSize = Dimension(300, 300)
      urlEditEvents.subscribe { value -> text = "Will try: $value" }
      newUrlEvents.subscribe { value -> text = "Cannot load: $value" }
    }
  }
  urlEditEvents.onNext("") // makes sure we receive the first empty url
}
