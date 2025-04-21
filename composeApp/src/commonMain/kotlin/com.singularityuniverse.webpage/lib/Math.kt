/**
 * Copyright (C) 2025  stefanus.ayudha@gmail.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package com.singularityuniverse.webpage.lib

import kotlin.math.exp
import kotlin.random.Random

typealias Vector = DoubleArray
typealias Matrix = Array<DoubleArray>

fun sigmoid(x: Double): Double = 1.0 / (1.0 + exp(-x))
fun sigmoidDerivative(x: Double): Double = x * (1 - x)

fun dot(a: Matrix, b: Vector): Vector {
    return a.map { row -> row.zip(b).sumOf { it.first * it.second } }.toDoubleArray()
}

fun transpose(m: Matrix): Matrix {
    val rows = m.size
    val cols = m[0].size
    return Array(cols) { i -> DoubleArray(rows) { j -> m[j][i] } }
}

fun randomMatrix(rows: Int, cols: Int): Matrix {
    return Array(rows) { DoubleArray(cols) { Random.nextDouble(-1.0, 1.0) } }
}