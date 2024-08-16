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

plugins {
  alias(libs.plugins.kotlin.jvm)
}

dependencies {
  api(libs.kotlinx.coroutines.test)
}
