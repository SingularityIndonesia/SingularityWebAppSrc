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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NeuralNetwork(
    inputSize: Int,
    private val outputSize: Int = 1,
    private val hiddenSize: Int = 11,
    private val learningRate: Double = 0.01
) {
    private var weightsInputHidden = randomMatrix(hiddenSize, inputSize)
    private var weightsHiddenOutput = randomMatrix(outputSize, hiddenSize)

    suspend fun train(inputs: Vector, targets: Vector, epochs: Int = 1000) = withContext(Dispatchers.Main) {
        println("Begin training")
        println("Target: ${targets.joinToString(",")}")
        println("Initial Bias: ${weightsHiddenOutput.joinToString(",") { it.joinToString(",") }}")
        repeat(epochs) {
            // Forward
            val hiddenInputs = dot(weightsInputHidden, inputs)
            val hiddenOutputs = hiddenInputs.map(::sigmoid).toDoubleArray()

            val finalInputs = dot(weightsHiddenOutput, hiddenOutputs)
            val finalOutputs = finalInputs.map(::sigmoid).toDoubleArray()

            // Errors
            val outputErrors = targets.zip(finalOutputs).map { (t, o) -> t - o }.toDoubleArray()
            val outputGradients =
                finalOutputs.mapIndexed { i, o -> sigmoidDerivative(o) * outputErrors[i] }.toDoubleArray()

            // Hidden layer errors
            val weightsHiddenOutputT = transpose(weightsHiddenOutput)
            val hiddenErrors = dot(weightsHiddenOutputT, outputGradients)
            val hiddenGradients =
                hiddenOutputs.mapIndexed { i, h -> sigmoidDerivative(h) * hiddenErrors[i] }.toDoubleArray()

            // Update weights
            for (i in weightsHiddenOutput.indices) {
                for (j in weightsHiddenOutput[i].indices) {
                    weightsHiddenOutput[i][j] += learningRate * outputGradients[i] * hiddenOutputs[j]
                }
            }

            for (i in weightsInputHidden.indices) {
                for (j in weightsInputHidden[i].indices) {
                    weightsInputHidden[i][j] += learningRate * hiddenGradients[i] * inputs[j]
                }
            }
        }

        println("Final Bias: ${weightsHiddenOutput.joinToString(",") { it.joinToString(",") }}")
    }

    fun predict(inputs: Vector): Vector {
        println("With Bias: ${weightsHiddenOutput.joinToString(",") { it.joinToString(",") }}")
        val hidden = dot(weightsInputHidden, inputs).map(::sigmoid).toDoubleArray()
        val result = dot(weightsHiddenOutput, hidden).map(::sigmoid).toDoubleArray()
        println("Result: ${result.joinToString(",")}")
        return result
    }
}