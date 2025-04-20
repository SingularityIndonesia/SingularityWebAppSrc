package com.singularityuniverse.webpage.application

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.singularityuniverse.webpage.core.Application
import com.singularityuniverse.webpage.core.design.TextIcon
import com.singularityuniverse.webpage.core.design.spaced8
import com.singularityuniverse.webpage.lib.NeuralNetwork
import kotlinx.coroutines.*

class NumberRecognition : Application() {
    override val title: String = "Number Recognition Neural Network"
    override val defaultMinSize: DpSize = DpSize(600.dp, 600.dp)

    private val brushSize = 20f
    private lateinit var localDensity: Density
    private var canvasSize: IntSize = IntSize.Zero
    private var neuralNetwork: NeuralNetwork? = null
    private var tokenList = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "nan")

    private fun prepare(bitmap: ImageBitmap) {
        if (this@NumberRecognition.neuralNetwork == null) {
            val inputSize = bitmap.width * bitmap.height
            println("Preparing NN of $inputSize units input")
            neuralNetwork = NeuralNetwork(
                inputSize = inputSize,
                outputSize = tokenList.size
            )
        }
    }

    private fun createDrawingBitmap(
        density: Density,
        points: List<Offset>,
        size: IntSize,
        scale: Float = .2f
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

        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(
                text = "Network Spec: 11 Hidden Layers. Dynamic input size, 11 outputs array.",
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                horizontalArrangement = spaced8
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
    }

    private val trainingResult = mutableStateOf("")
    private val trainingPreview = mutableStateOf<ImageBitmap?>(null)
    private suspend fun train(target: DoubleArray, image: ImageBitmap) {
        trainingResult.value = "Training in progress..\nIt might freeze the browser, just wait."

        trainingPreview.value = image

        // prepare
        prepare(image)
        val nn = this@NumberRecognition.neuralNetwork!!

        delay(300)
        val input = imageBitmapToGrayscaleDoubleArray(image)
        nn.train(input, target)
        trainingPreview.value = null
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun Training(
        modifier: Modifier = Modifier
    ) {
        val scope = rememberCoroutineScope()
        var target by remember { mutableStateOf(TextFieldValue("0")) }
        val trainingPoints = remember { mutableStateListOf<Offset>() }
        val trainingBuffer = remember { mutableStateListOf<List<Offset>>() }

        Column(
            modifier = modifier
                .then(Modifier.padding(8.dp)),
            verticalArrangement = spaced8
        ) {
            Text(text = "Training")
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
                    trainingBuffer.add(trainingPoints.toList())
                    trainingPoints.clear()

                    // wait for 10 picture
                    if (trainingBuffer.size < 10) {
                        trainingResult.value = "Draw another"
                        return@Drawing
                    }
                    trainingResult.value = "Training in progress.. Please wait.."

                    scope.launch {
                        val target = target.text.toIntOrNull()

                        val targetArray = if (target == null) {
                            (0..10).map { if (it == 10) 1.0 else 0.0 }
                        } else {
                            (0..10).map { if (it == target) 1.0 else 0.0 }
                        }.toDoubleArray()

                        repeat(trainingBuffer.size) {
                            val array = trainingBuffer.first()
                            trainingBuffer.removeAt(0)

                            val image = createDrawingBitmap(localDensity, array, canvasSize)
                            train(targetArray, image)
                        }

                        trainingResult.value = "Finish"
                    }
                }
            )

            LazyRow(
                modifier = Modifier.wrapContentHeight(),
                horizontalArrangement = spaced8,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (trainingPreview.value != null) {
                    item {
                        Image(
                            modifier = Modifier
                                .size(40.dp)
                                .border(BorderStroke(.5.dp, Color.LightGray)),
                            contentScale = ContentScale.Fit,
                            bitmap = trainingPreview.value!!,
                            contentDescription = null
                        )
                    }
                }
                item {
                    Text(
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.caption,
                        text = trainingResult.value
                    )
                }
            }

            LazyRow(
                modifier = Modifier.wrapContentHeight().align(Alignment.End),
                horizontalArrangement = spaced8,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(trainingBuffer.size) {
                    val image = createDrawingBitmap(localDensity, trainingBuffer[it], canvasSize)
                    Image(
                        modifier = Modifier
                            .size(30.dp)
                            .border(BorderStroke(.5.dp, Color.LightGray))
                            .onClick {
                                trainingBuffer.removeAt(it)
                            },
                        contentScale = ContentScale.Fit,
                        bitmap = image,
                        contentDescription = null
                    )
                }
            }
        }
    }

    private val predictingPoints = mutableStateListOf<Offset>()
    private var predictionJob: Deferred<Unit>? = null
    private val predictionResult = mutableStateOf("")
    private val predictionPreview = mutableStateOf<ImageBitmap?>(null)
    private suspend fun predict() = coroutineScope {
        predictionResult.value = "Calculating.."

        predictionJob?.cancel()

        predictionJob = async {
            val image = createDrawingBitmap(localDensity, predictingPoints, canvasSize)
            predictionPreview.value = image

            // prepare
            prepare(image)
            val nn = this@NumberRecognition.neuralNetwork!!

            delay(300)
            val input = imageBitmapToGrayscaleDoubleArray(image)
            val prediction = nn.predict(input)
            val predictionMax = prediction.max()
            val maxIndex = prediction.indexOfFirst { it == predictionMax }
            val result = tokenList[maxIndex]

            ensureActive()
            predictionResult.value = "Result = $result"
        }
    }

    private fun clearPredictionCanvas() {
        predictionPreview.value = null
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
            verticalArrangement = spaced8
        ) {
            Text(text = "Predicting")
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
                        predict()
                    }
                }
            )

            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = spaced8,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (predictionPreview.value != null) {
                    Image(
                        modifier = Modifier
                            .size(40.dp)
                            .border(BorderStroke(.5.dp, Color.LightGray)),
                        contentScale = ContentScale.Fit,
                        bitmap = predictionPreview.value!!,
                        contentDescription = null
                    )
                }
                Spacer(Modifier.weight(1f))
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
        TextIcon(modifier = modifier, text = "N", onClick = onClick)
    }
}