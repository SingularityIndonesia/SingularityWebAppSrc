package com.singularityuniverse.webpage.application

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.*
import com.singularityuniverse.webpage.core.Application
import com.singularityuniverse.webpage.lib.NeuralNetwork
import kotlinx.coroutines.*
import kotlin.math.roundToInt

class NumberRecognition : Application() {
    override val title: String = "Number Recognition Neural Network"
    override val defaultMinSize: DpSize = DpSize(600.dp, 550.dp)

    private val brushSize = 20f
    private lateinit var localDensity: Density
    private var canvasSize: IntSize = IntSize.Zero
    private var neuralNetwork: NeuralNetwork? = null

    private fun prepare(bitmap: ImageBitmap) {
        if (this@NumberRecognition.neuralNetwork == null) {
            val inputSize = bitmap.width * bitmap.height
            println("Preparing NN of $inputSize units input")
            neuralNetwork = NeuralNetwork(
                inputSize = inputSize,
            )
        }
    }

    private fun createDrawingBitmap(
        density: Density,
        points: List<Offset>,
        size: IntSize,
        scale: Float = .5f
    ): ImageBitmap {
        // Scale the canvas size
        val scaledWidth = (size.width * scale).toInt()
        val scaledHeight = (size.height * scale).toInt()
        val scaledBitmap = ImageBitmap(scaledWidth, scaledHeight, ImageBitmapConfig.Argb8888)
        val canvas = Canvas(scaledBitmap)

        val paint = Paint().apply {
            color = Color.Black
            style = PaintingStyle.Fill
            isAntiAlias = true
        }

        // Scale and draw points
        points.forEach { point ->
            val scaledOffset = Offset(point.x * scale, point.y * scale)
            val scaledRadius = brushSize * density.density * scale
            canvas.drawCircle(
                center = scaledOffset,
                radius = scaledRadius, // also scale the radius
                paint = paint
            )
        }

        return scaledBitmap
    }

    private fun imageBitmapToGrayscaleDoubleArray(image: ImageBitmap): DoubleArray {
        val pixelMap = image.toPixelMap()
        val width = pixelMap.width
        val height = pixelMap.height

        return DoubleArray(width * height) { index ->
            val x = index % width
            val y = index / width
            val color = pixelMap[x, y]

            // Grayscale conversion: 0.299*R + 0.587*G + 0.114*B
            val gray = 0.299 * color.red + 0.587 * color.green + 0.114 * color.blue
            gray // Already in range 0.0..1.0
        }
    }

    @Composable
    override fun Draw(modifier: Modifier) {
        val density = LocalDensity.current

        LaunchedEffect(density.density) {
            localDensity = density
        }

        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Training(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .border(BorderStroke(.5.dp, Color.LightGray))
            )
            Predicting(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .border(BorderStroke(.5.dp, Color.LightGray))
            )
        }
    }

    private val predictingPoints = mutableStateListOf<Offset>()
    private var predictionJob: Deferred<Unit>? = null
    private val predictionResult = mutableStateOf("")
    private suspend fun calculate() = coroutineScope {
        predictionResult.value = "Calculating.."

        predictionJob?.cancel()

        predictionJob = async {
            delay(300)
            val image = createDrawingBitmap(localDensity, predictingPoints, canvasSize)

            // prepare
            prepare(image)
            val nn = this@NumberRecognition.neuralNetwork!!

            val input = imageBitmapToGrayscaleDoubleArray(image)
            val prediction = nn.predict(input)[0]

            ensureActive()
            predictionResult.value = "Result = ${prediction.roundToInt()}"
        }
    }

    private fun clearPredictionCanvas() {
        predictionJob?.cancel()
        predictingPoints.clear()
        predictionResult.value = ""
    }

    @Composable
    private fun Predicting(
        modifier: Modifier = Modifier
    ) {
        val scope = rememberCoroutineScope()
        Column(
            modifier = modifier
                .then(Modifier.padding(8.dp)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Predicting")
            TextField(
                modifier = Modifier.width(300.dp),
                enabled = false,
                value = TextFieldValue(predictionResult.value),
                onValueChange = { new: TextFieldValue ->

                }
            )
            Drawing(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .onSizeChanged {
                        canvasSize = it
                    },
                title = "Draw number to predict",
                points = predictingPoints,
                onStart = {
                    predictionResult.value = "Drawing.."
                },
                onDrag = { change, _ ->
                    predictingPoints.add(change.position)
                },
                onDragEnd = {
                    scope.launch {
                        calculate()
                    }
                }
            )

            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        clearPredictionCanvas()
                    }
                ) {
                    Text(
                        modifier = Modifier,
                        text = "Reset"
                    )
                }
            }
        }
    }

    private val trainingPoints = mutableStateListOf<Offset>()
    private var trainingJob: Deferred<Unit>? = null
    private val trainingResult = mutableStateOf("")

    private suspend fun train(target: Double = 0.0) = coroutineScope {
        trainingResult.value = "Training in progress..\nIt might freeze the browser, just wait."

        trainingJob?.cancel()

        trainingJob = async {
            delay(300)
            val image = createDrawingBitmap(localDensity, trainingPoints, canvasSize)
            // prepare
            prepare(image)
            val nn = this@NumberRecognition.neuralNetwork!!

            val input = imageBitmapToGrayscaleDoubleArray(image)
            val targetVec = doubleArrayOf(target)
            nn.train(input, targetVec)

            trainingResult.value = "Finish"
        }
    }

    private fun clearTrainingCanvas() {
        trainingJob?.cancel()
        trainingPoints.clear()
        trainingResult.value = ""
    }

    @Composable
    private fun Training(
        modifier: Modifier = Modifier
    ) {
        val scope = rememberCoroutineScope()
        var target by remember { mutableStateOf(TextFieldValue("0")) }

        Column(
            modifier = modifier
                .then(Modifier.padding(8.dp)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Training")
            TextField(
                modifier = Modifier.width(300.dp),
                value = target,
                label = {
                    Text("Enter number or `nan`")
                },
                onValueChange = { new: TextFieldValue ->
                    target = new
                }
            )
            Drawing(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .onSizeChanged {
                        canvasSize = it
                    },
                title = "Draw ${target.text}",
                points = trainingPoints,
                onStart = {
                    trainingResult.value = "Drawing.."
                },
                onDrag = { change, _ ->
                    trainingPoints.add(change.position)
                },
                onDragEnd = {
                    scope.launch {
                        train(target.text.toDoubleOrNull() ?: 11.0) // 11 == NaN
                    }
                }
            )

            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.caption,
                    text = trainingResult.value
                )
                OutlinedButton(
                    onClick = {
                        clearTrainingCanvas()
                    }
                ) {
                    Text(
                        modifier = Modifier,
                        text = "Clear"
                    )
                }
            }
        }
    }

    @Composable
    private fun Drawing(
        modifier: Modifier = Modifier,
        title: String,
        points: SnapshotStateList<Offset>,
        onStart: () -> Unit,
        onDrag: (PointerInputChange, Offset) -> Unit,
        onDragEnd: () -> Unit
    ) {
        val textMeasurer = rememberTextMeasurer()

        Canvas(
            modifier = Modifier
                .border(border = BorderStroke(.5.dp, Color.LightGray))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { onStart.invoke() },
                        onDrag = onDrag,
                        onDragEnd = onDragEnd
                    )
                }
                .then(modifier)

        ) {
            drawText(
                topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                textMeasurer = textMeasurer,
                text = title
            )
            clipRect {
                points.forEach { point ->
                    drawCircle(
                        color = Color.Black,
                        radius = brushSize.dp.toPx(),
                        center = point
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Icon(modifier: Modifier, onClick: () -> Unit) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colors.secondary)
                .onClick { onClick.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier,
                text = "N",
                style = MaterialTheme.typography.overline,
                color = MaterialTheme.colors.onSecondary,
                fontSize = 32.sp
            )
        }
    }
}