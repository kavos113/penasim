package com.example.penasim.ui.command

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.penasim.R
import com.example.penasim.domain.League
import com.example.penasim.domain.Team
import com.example.penasim.ui.navigation.NavigationDestination
import com.example.penasim.ui.theme.catcherColor
import com.example.penasim.ui.theme.infielderColor
import com.example.penasim.ui.theme.outfielderColor
import com.example.penasim.ui.theme.pitcherColor
import com.example.penasim.ui.theme.playerBorderColor

object FielderDestination : NavigationDestination {
    override val route: String = "fielder"
    override val titleResId: Int = R.string.fielder
}

@Composable
fun FielderScreen(
    modifier: Modifier = Modifier,
    commandViewModel: CommandViewModel = hiltViewModel()
) {
    val uiState by commandViewModel.uiState.collectAsState()
    FielderContent(
        uiState = uiState,
        modifier = modifier
    )
}

@Composable
private fun FielderContent(
    uiState: CommandUiState,
    modifier: Modifier = Modifier
) {
    Row (
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        modifier = modifier
    ) {
        OrderList(
            fielders = uiState.getDisplayFielders(uiState.orderFielderAppointments),
            modifier = Modifier
                .weight(5f)
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    drawLine(
                        color = playerBorderColor,
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = strokeWidth
                    )

                    drawLine(
                        color = playerBorderColor,
                        start = Offset(size.width, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = strokeWidth
                    )
                }
        )
        BenchList(
            fielders = uiState.getDisplayFielders(uiState.benchFielderAppointments),
            modifier = Modifier
                .weight(3f)
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    drawLine(
                        color = playerBorderColor,
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = strokeWidth
                    )

                    drawLine(
                        color = playerBorderColor,
                        start = Offset(size.width, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = strokeWidth
                    )
                }
        )
        SubstituteList(
            fielders = uiState.getDisplayFielders(uiState.subFielderAppointments),
            modifier = Modifier
                .weight(3f)
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    drawLine(
                        color = playerBorderColor,
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = strokeWidth
                    )

                    drawLine(
                        color = playerBorderColor,
                        start = Offset(size.width, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = strokeWidth
                    )
                }
        )
    }
}

@Composable
private fun OrderList(
    fielders: List<DisplayFielder>,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text("オーダー")
        repeat(fielders.size) {
            OrderPlayerItem(player = fielders[it])
        }
    }
}

@Composable
private fun BenchList(
    fielders: List<DisplayFielder>,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text("ベンチ")
        repeat(fielders.size) {
            SubstitutePlayerItem(displayName = fielders[it].displayName, color = fielders[it].color)
        }
    }
}

@Composable
private fun SubstituteList(
    fielders: List<DisplayFielder>,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text("2軍")
        LazyColumn {
            items(fielders) { fielder ->
                SubstitutePlayerItem(displayName = fielder.displayName, color = fielder.color)
            }
        }
    }
}

@Composable
private fun OrderPlayerItem(
    player: DisplayFielder,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .border(
                    width = 1.dp,
                    color = playerBorderColor,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = player.number.toString(),
                fontSize = 16.sp
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(3f)
                .height(40.dp)
                .border(
                    width = 1.dp,
                    color = playerBorderColor,
                    shape = RoundedCornerShape(4.dp)
                )
                .background(color = player.color)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = player.displayName,
                fontSize = 16.sp
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .border(
                    width = 1.dp,
                    color = playerBorderColor,
                    shape = RoundedCornerShape(4.dp)
                )
                .background(color = player.color)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = player.position,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun SubstitutePlayerItem(
    displayName: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(40.dp)
            .border(
                width = 1.dp,
                color = playerBorderColor,
                shape = RoundedCornerShape(4.dp)
            )
            .background(color = color)
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .fillMaxWidth()
    ) {
        Text(displayName)
    }
}

@Preview(showBackground = true)
@Composable
fun FielderScreenPreview() {
    FielderContent(
        uiState = CommandUiState(
            team = Team(id = 0, name = "Example Team", league = League.L1)

        )
    )
}

@Preview(showBackground = true)
@Composable
fun OrderListPreview() {
    OrderList(
        fielders = listOf(
            DisplayFielder("Player 1", "投", 1, true, pitcherColor),
            DisplayFielder("Player 2", "捕", 2, true, catcherColor),
            DisplayFielder("Player 3", "一", 3, true, infielderColor),
            DisplayFielder("Player 4", "二", 4, true, infielderColor),
            DisplayFielder("Player 5", "三", 5, true, infielderColor),
            DisplayFielder("Player 6", "遊", 6, true, infielderColor),
            DisplayFielder("Player 7", "左", 7, true, outfielderColor),
            DisplayFielder("Player 8", "中", 8, true, outfielderColor),
            DisplayFielder("Player 9", "右", 9, true, outfielderColor),
        )
    )
}

@Preview(showBackground = true)
@Composable
fun BenchListPreview() {
    BenchList(
        fielders = listOf(
            DisplayFielder("Player 10", "捕", 10, true, catcherColor),
            DisplayFielder("Player 11", "一", 11, true, infielderColor),
            DisplayFielder("Player 12", "外", 12, true, outfielderColor),
            DisplayFielder("Player 13", "三", 13, true, infielderColor),
            DisplayFielder("Player 14", "外", 14, true, infielderColor),
            DisplayFielder("Player 15", "遊", 15, true, infielderColor),
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SubstituteListPreview() {
    SubstituteList(
        fielders = listOf(
            DisplayFielder("Player 16", "捕", 16, false, catcherColor),
            DisplayFielder("Player 17", "一", 17, false, infielderColor),
            DisplayFielder("Player 18", "外", 18, false, outfielderColor),
            DisplayFielder("Player 19", "三", 19, false, infielderColor),
            DisplayFielder("Player 20", "外", 20, false, outfielderColor),
            DisplayFielder("Player 21", "遊", 21, false, infielderColor),
            DisplayFielder("Player 22", "二", 22, false, infielderColor),
        )
    )
}