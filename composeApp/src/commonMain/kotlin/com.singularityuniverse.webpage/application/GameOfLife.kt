package com.singularityuniverse.webpage.application

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singularityuniverse.webpage.core.Application
import kotlin.math.absoluteValue

class GameOfLife : Application() {
    override val title: String = "Conway Game of Life"
    override val defaultMinSize: DpSize = DpSize(480.dp, 530.dp)

    private val matrixSize = 30
    private fun zeros() = Array<Int>(matrixSize * matrixSize) { 0 }
        .toIntArray()
        .toTypedArray()

    private val arrayState = mutableStateListOf<Int>(*zeros())

    fun toggle(index: Int) {
        arrayState[index] = (arrayState[index] - 1).absoluteValue
    }

    fun evaluate(list: List<Int>): List<Int> {
        return list.mapIndexed { i, state ->
            // coordinate
            val x = i.div(matrixSize)
            val y = i.mod(matrixSize)

            val neighborCount = listOf(
                // sum of top neighbor
                ((x - 1) * matrixSize + y).let { it - 1..it + 1 }
                    .fold(0) { acc, i -> acc + (list.getOrNull(i) ?: 0) },
                // sum of side neighbor
                (i - 1..i + 1).fold(0) { acc, i -> acc + (list.getOrNull(i) ?: 0) } - state,
                // sum of bellow neighbor
                ((x + 1) * matrixSize + y).let { it - 1..it + 1 }
                    .fold(0) { acc, i -> acc + (list.getOrNull(i) ?: 0) },
            ).sum()

            // next state
            when {
                // survive
                state == 1 && neighborCount in 2..3 -> 1

                // reproduce
                state == 0 && neighborCount == 3 -> 1

                // underpopulated || overpopulated-> die
                else -> 0
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Draw(modifier: Modifier) {
        Column(
            modifier = modifier
                .padding(8.dp)
        ) {
            Spacer(Modifier.size(16.dp))

            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                LazyHorizontalGrid(
                    modifier = Modifier.wrapContentSize(),
                    rows = GridCells.Fixed(matrixSize)
                ) {
                    items(arrayState.size) {
                        val isAlive = arrayState[it] == 1
                        Box(
                            modifier = Modifier.size(15.dp)
                                .border(BorderStroke(.5.dp, Color.LightGray))
                                .background(if (isAlive) Color.Black else Color.White)
                                .onClick { toggle(it) },
                            contentAlignment = Alignment.Center
                        ) {}
                    }
                }
            }

            Spacer(Modifier.size(16.dp))

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        arrayState.clear()
                        arrayState.addAll(zeros())
                    }
                ) {
                    Text("Reset")
                }
                Button(
                    onClick = {
                        val result = evaluate(arrayState)
                        arrayState.clear()
                        arrayState.addAll(result)
                    }
                ) {
                    Text("Evaluate")
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
                .background(MaterialTheme.colors.background)
                .onClick { onClick.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier,
                text = "GoL",
                style = MaterialTheme.typography.overline,
                color = MaterialTheme.colors.onBackground,
                fontSize = 20.sp
            )
        }
    }
}