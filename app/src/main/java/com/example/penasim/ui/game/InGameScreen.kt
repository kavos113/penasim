package com.example.penasim.ui.game

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.penasim.domain.Position
import com.example.penasim.ui.common.DisplayFielder
import com.example.penasim.ui.common.OrderPlayerItem
import com.example.penasim.ui.theme.catcherColor
import com.example.penasim.ui.theme.infielderColor
import com.example.penasim.ui.theme.outfielderColor
import com.example.penasim.ui.theme.pitcherColor

@Composable
private fun InGameContent(
    inGameInfo: InGameInfo,
    modifier: Modifier = Modifier
) {

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