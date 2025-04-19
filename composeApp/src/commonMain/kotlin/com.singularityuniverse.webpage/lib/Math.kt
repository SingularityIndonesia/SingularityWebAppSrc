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