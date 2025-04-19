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
        println("Initial Magnitude: ${weightsHiddenOutput.joinToString(",") { it.joinToString(",") }}")
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

        println("Final Magnitude: ${weightsHiddenOutput.joinToString(",") { it.joinToString(",") }}")
    }

    fun predict(inputs: Vector): Vector {
        println("With Magnitude: ${weightsHiddenOutput.joinToString(",") { it.joinToString(",") }}")
        val hidden = dot(weightsInputHidden, inputs).map(::sigmoid).toDoubleArray()
        val result = dot(weightsHiddenOutput, hidden).map(::sigmoid).toDoubleArray()
        println("Result: ${result.joinToString(",")}")
        return result
    }
}