package com.knightleo.tictactoe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private fun MutableList<Boolean?>.nullifyBoard(): MutableList<Boolean?> {
    clear()
    repeat(3 * 3) {
        add(null)
    }
    return this
}

private fun List<Boolean?>.checkEndGame(playerTurn: Boolean): Boolean? {
    if (this[0] == playerTurn && this[4] == playerTurn && this[8] == playerTurn) return true
    else if (this[2] == playerTurn && this[4] == playerTurn && this[6] == playerTurn) return true
    fun checkRow(row: Int): Boolean {
        repeat(3) {
            if (this[row * 3 + it] != playerTurn) {
                return false
            }
        }
        return true
    }

    fun checkCol(col: Int): Boolean {
        repeat(3) {
            if (this[it * 3 + col] != playerTurn) {
                return false
            }
        }
        return true
    }
    for (i in 0 until 3) {
        if (checkRow(i) || checkCol(i)) {
            return true
        }
    }
    return if (all { it != null }) null
    else false
}

@Preview
@Composable
fun TicTacToeScreen(modifier: Modifier = Modifier) {
    var playerTurn by remember { mutableStateOf(true) }
    var gameEnded: Boolean? by remember { mutableStateOf(false) }
    val moves = remember {
        mutableStateListOf<Boolean?>().nullifyBoard()
    }
    val click: (Int, Int) -> Boolean = { row, col ->
        if (moves[row * 3 + col] != null) false
        else {
            moves[row * 3 + col] = playerTurn
            playerTurn = !playerTurn
            true
        }
    }
    LaunchedEffect(key1 = playerTurn) {
        val state = moves.checkEndGame(!playerTurn)
        if (state == true) {
            gameEnded = true
        } else if (state == null) {
            gameEnded = null
        } else if (!playerTurn) {
            launch {
                delay(Random.nextLong(1000))
                while (!click(Random.nextInt(3), Random.nextInt(3))) {
                    delay(Random.nextLong(100))
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        Header(playerTurn)
        Board(moves) { r, c ->
            click(r, c)
        }
        if (gameEnded == null || gameEnded == true) {
            val text = if (gameEnded == null) {
                "Its a draw!"
            } else {
                val name = if (!playerTurn) "Player" else "Ai"
                "$name is the winner!"
            }
            Text(text = text, style = MaterialTheme.typography.headlineLarge)
            Button(
                onClick = {
                    gameEnded = false
                    moves.nullifyBoard()
                    playerTurn = true
                }
            ) {
                Text(text = "Restart")
            }
        } else if (!playerTurn) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun Header(playerTurn: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(25.dp)
    ) {
        Text(text = "Tic Tac Toe", style = MaterialTheme.typography.titleLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val (playerColor, aiColor) = if (playerTurn) Color.Blue to Color.Gray else Color.Gray to Color.Red
            Box(
                modifier = Modifier
                    .background(playerColor)
                    .size(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Player")
            }
            Box(
                modifier = Modifier
                    .background(aiColor)
                    .size(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Ai")
            }
        }
    }
}

@Composable
fun Board(moves: List<Boolean?>, onClick: (Int, Int) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.width(IntrinsicSize.Max)
    ) {
        repeat(3) { row ->
            Row {
                repeat(3) { col ->
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .background(Color.LightGray)
                            .clickable {
                                onClick(row, col)
                            }
                            .height(IntrinsicSize.Max)
                    ) {
                        when (moves[row * 3 + col]) {
                            true -> Image(
                                painter = painterResource(R.drawable.ic_o),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(Color.Blue),
                                modifier = Modifier.fillMaxSize()
                            )

                            false -> Image(
                                painter = painterResource(R.drawable.ic_close),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(Color.Red),
                                modifier = Modifier.fillMaxSize()
                            )

                            null -> {}
                        }
                        if (col != 0) VerticalDivider(color = Color.Black)
                    }
                }
            }
            if (row < 2) HorizontalDivider(color = Color.Black)
        }
    }
}