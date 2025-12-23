package com.example.penasim.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.penasim.domain.Position
import com.example.penasim.ui.common.DisplayFielder
import com.example.penasim.ui.common.OrderPlayerItem
import com.example.penasim.ui.common.SimplePlayerItem
import com.example.penasim.ui.theme.blankColor
import com.example.penasim.ui.theme.catcherColor
import com.example.penasim.ui.theme.infielderColor
import com.example.penasim.ui.theme.outColor
import com.example.penasim.ui.theme.outfielderColor
import com.example.penasim.ui.theme.pitcherColor
import kotlin.math.cos
import kotlin.math.sin

@Composable
private fun InGameContent(
    inGameInfo: InGameInfo,
    modifier: Modifier = Modifier
) {

}

@Composable
private fun PlayerInfoContent(
    homePlayers: List<DisplayFielder>,
    awayPlayers: List<DisplayFielder>,
    homeActiveNumber: Int?,
    awayActiveNumber: Int?,
    firstBase: DisplayFielder?,
    secondBase: DisplayFielder?,
    thirdBase: DisplayFielder?,
    outCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        OrderList(
            fielders = awayPlayers,
            activeNumber = awayActiveNumber,
            modifier = Modifier
                .weight(1f)
        )
        Spacer(
            modifier = Modifier.weight(0.5f)
        )
        BaseInfo(
            firstBase = firstBase,
            secondBase = secondBase,
            thirdBase = thirdBase,
            outCount = outCount,
            modifier = Modifier
                .weight(1f)
        )
        Spacer(
            modifier = Modifier.weight(0.5f)
        )
        OrderList(
            fielders = homePlayers,
            activeNumber = homeActiveNumber,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
private fun BaseInfo(
    firstBase: DisplayFielder?,
    secondBase: DisplayFielder?,
    thirdBase: DisplayFielder?,
    outCount: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(100.dp),
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .height(100.dp)
            ) {
                TriangleLayout {
                    if (firstBase != null) {
                        SimplePlayerItem(
                            displayName = firstBase.displayName,
                            color = firstBase.color,
                            modifier = Modifier
                                .layoutId(TrianglePosition.BottomRight)
                                .width(120.dp)
                        )
                    }
                    if (secondBase != null) {
                        SimplePlayerItem(
                            displayName = secondBase.displayName,
                            color = secondBase.color,
                            modifier = Modifier
                                .layoutId(TrianglePosition.Top)
                                .width(120.dp)
                        )
                    }
                    if (thirdBase != null) {
                        SimplePlayerItem(
                            displayName = thirdBase.displayName,
                            color = thirdBase.color,
                            modifier = Modifier
                                .layoutId(TrianglePosition.BottomLeft)
                                .width(120.dp)
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "O",
                    fontSize = 24.sp
                )
                val outColors = when (outCount) {
                    0 -> listOf(blankColor, blankColor)
                    1 -> listOf(outColor, blankColor)
                    else -> listOf(outColor, outColor)
                }
                Row {
                    repeat(outColors.size) { i ->
                        Canvas(modifier = Modifier.size(24.dp)) {
                            drawCircle(
                                color = outColors[i],
                                radius = size.minDimension / 2
                            )
                        }
                    }
                }
            }
        }
    }
}

private enum class TrianglePosition {
    Top,
    BottomLeft,
    BottomRight
}

@Composable
private fun TriangleLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeable = measurables.map { it.layoutId as TrianglePosition to it.measure(constraints) }

        layout(constraints.maxWidth, constraints.maxHeight) {
            val width = constraints.maxWidth.toFloat()
            val height = constraints.maxHeight.toFloat()

            val positions = mapOf(
                TrianglePosition.Top to Offset(width / 2f, 0f),
                TrianglePosition.BottomLeft to Offset(0f, height),
                TrianglePosition.BottomRight to Offset(width, height)
            )

            placeable.forEach { (pos, component) ->
                val coord = positions[pos] ?: Offset.Zero
                component.placeRelative(
                    x = (coord.x - component.width / 2).toInt(),
                    y = (coord.y - component.height / 2).toInt()
                )
            }
        }
    }
}

// activeNumber: 1-9
@Composable
private fun OrderList(
    fielders: List<DisplayFielder>,
    activeNumber: Int?,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        repeat(fielders.size) {
            OrderPlayerItem(
                player = fielders[it],
                modifier = Modifier,
                isActive = it + 1 == activeNumber
            )
        }
    }
}

private val SAMPLE_ORDER = listOf(
    DisplayFielder(
        0,
        "Pitcher",
        Position.PITCHER,
        1,
        pitcherColor
    ),
    DisplayFielder(
        1,
        "Player 2",
        Position.CATCHER,
        2,
        catcherColor
    ),
    DisplayFielder(
        2,
        "Player 3",
        Position.FIRST_BASEMAN,
        3,
        infielderColor
    ),
    DisplayFielder(
        3,
        "Player 4",
        Position.SECOND_BASEMAN,
        4,
        infielderColor
    ),
    DisplayFielder(
        4,
        "Player 5",
        Position.THIRD_BASEMAN,
        5,
        infielderColor
    ),
    DisplayFielder(
        5,
        "Player 6",
        Position.SHORTSTOP,
        6,
        infielderColor
    ),
    DisplayFielder(
        6,
        "Player 7",
        Position.LEFT_FIELDER,
        7,
        infielderColor
    ),
    DisplayFielder(
        7,
        "Player 8",
        Position.CENTER_FIELDER,
        8,
        outfielderColor
    ),
    DisplayFielder(
        8,
        "Player 9",
        Position.RIGHT_FIELDER,
        9,
        outfielderColor
    ),
)

@Preview
@Composable
private fun OrderListPreview() {
    OrderList(
        fielders = SAMPLE_ORDER,
        activeNumber = 2
    )
}

@Preview
@Composable
private fun BaseInfoPreview() {
    BaseInfo(
        firstBase = DisplayFielder(
            6,
            "Player 7",
            Position.LEFT_FIELDER,
            7,
            infielderColor
        ),
        secondBase = DisplayFielder(
            7,
            "Player 8",
            Position.CENTER_FIELDER,
            8,
            outfielderColor
        ),
        thirdBase = DisplayFielder(
            8,
            "Player 9",
            Position.RIGHT_FIELDER,
            9,
            outfielderColor
        ),
        outCount = 1
    )
}

@Preview
@Composable
private fun PlayerInfoContentPreview() {
    PlayerInfoContent(
        homePlayers = SAMPLE_ORDER,
        awayPlayers = SAMPLE_ORDER,
        homeActiveNumber = 3,
        awayActiveNumber = null,
        firstBase = null,
        secondBase = DisplayFielder(
            7,
            "Player 8",
            Position.CENTER_FIELDER,
            8,
            outfielderColor
        ),
        thirdBase = DisplayFielder(
            8,
            "Player 9",
            Position.RIGHT_FIELDER,
            9,
            outfielderColor
        ),
        outCount = 1,
    )
}