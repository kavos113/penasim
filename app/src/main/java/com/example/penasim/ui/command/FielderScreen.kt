package com.example.penasim.ui.command

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.penasim.R
import com.example.penasim.domain.OrderType
import com.example.penasim.domain.Player
import com.example.penasim.domain.PlayerPosition
import com.example.penasim.domain.Position
import com.example.penasim.domain.toShortJa
import com.example.penasim.ui.navigation.NavigationDestination
import com.example.penasim.ui.theme.catcherColor
import com.example.penasim.ui.theme.infielderColor
import com.example.penasim.ui.theme.outfielderColor
import com.example.penasim.ui.theme.pitcherColor
import com.example.penasim.ui.theme.playerBorderColor
import com.example.penasim.ui.theme.substituteBackgroundColor
import kotlinx.coroutines.launch

object FielderDestination : NavigationDestination {
    override val route: String = "fielder"
    override val titleResId: Int = R.string.fielder
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FielderScreen(
    modifier: Modifier = Modifier,
    commandViewModel: CommandViewModel
) {
    val uiState by commandViewModel.uiState.collectAsState()

    val tabs = listOf(
        R.string.fielder_order,
        R.string.fielder_order_left,
        R.string.fielder_order_dh,
        R.string.fielder_order_dh_left
    )
    
    val selectedTabIndex = rememberPagerState { tabs.size }
    val tabScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SecondaryTabRow(
            selectedTabIndex = selectedTabIndex.currentPage,
            modifier = Modifier.fillMaxWidth(),
        ) {
            tabs.forEachIndexed { index, titleResId ->
                Tab(
                    selected = selectedTabIndex.currentPage == index,
                    onClick = {
                        tabScope.launch {
                            selectedTabIndex.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(stringResource(titleResId))
                    }
                )
            }
        }

        HorizontalPager(
            state = selectedTabIndex,
            modifier = Modifier
                .fillMaxSize()
        ) { page ->
            FielderContent(
                uiState = uiState,
                onPlayerClick = { playerId ->
                    commandViewModel.selectFielder(playerId)
                },
                modifier = Modifier.fillMaxSize(),
                orderType = getOrderType(page)
            )
        }
    }
}

private fun getOrderType(tabIndex: Int): OrderType = when(tabIndex) {
    0 -> OrderType.NORMAL
    1 -> OrderType.LEFT
    2 -> OrderType.DH
    3 -> OrderType.LEFT_DH
    else -> OrderType.NORMAL
}

@Composable
private fun FielderContent(
    uiState: CommandUiState,
    onPlayerClick: (Int) -> Unit,
    orderType: OrderType,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            modifier = modifier
        ) {
            OrderList(
                fielders = uiState.getDisplayFielders(uiState.getOrderFielderAppointments(orderType)),
                onItemClick = onPlayerClick,
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
                fielders = uiState.getDisplayFielders(uiState.getBenchFielderAppointments(orderType)),
                onItemClick = onPlayerClick,
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
                fielders = uiState.getDisplayFielders(uiState.getSubFielderAppointments(orderType)),
                onItemClick = onPlayerClick,
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
                    .background(substituteBackgroundColor)
            )
        }

        if (uiState.selectedFielderId != null) {
            val playerDetail = uiState.getDisplayPlayerDetail(uiState.selectedFielderId)
            if (playerDetail != null) {
                Box(
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .border(2.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(0.7f)
                ) {
                    FielderDetail(playerDetail = playerDetail)
                }
            }
        }
    }
}

@Composable
private fun OrderList(
    fielders: List<DisplayFielder>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(stringResource(R.string.order))
        repeat(fielders.size) {
            OrderPlayerItem(
                player = fielders[it],
                modifier = Modifier
                    .clickable { onItemClick(fielders[it].id) }
            )
        }
    }
}

@Composable
private fun BenchList(
    fielders: List<DisplayFielder>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(stringResource(R.string.bench))
        repeat(fielders.size) {
            SimplePlayerItem(
                displayName = fielders[it].displayName,
                color = fielders[it].color,
                modifier = Modifier
                    .clickable { onItemClick(fielders[it].id) }
            )
        }
    }
}

@Composable
private fun SubstituteList(
    fielders: List<DisplayFielder>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(stringResource(R.string.substitute))
        LazyColumn {
            items(fielders) { fielder ->
                SimplePlayerItem(
                    displayName = fielder.displayName,
                    color = fielder.color,
                    modifier = Modifier
                        .clickable { onItemClick(fielder.id) }
                )
            }
        }
    }
}

@Composable
private fun FielderDetail(
    playerDetail: DisplayPlayerDetail,
    modifier: Modifier = Modifier
) {
    Row {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .weight(2f)
                .padding(12.dp)
        ) {
            SimplePlayerItem(
                displayName = playerDetail.player.firstName,
                color = playerDetail.color,
            )
            Text(
                text = ".306",
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "12本 55点",
                fontSize = 20.sp,
            )
            Text(
                text = "2盗",
                fontSize = 20.sp,
            )

            Row {
                val (first, second) = playerDetail.positions.withIndex().partition { it.index % 2 == 0 }
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .weight(1f)
                ) {
                    repeat(first.size) {
                        DefenseStatus(position = first[it].value)
                    }
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .weight(1f)
                ) {
                    repeat(second.size) {
                        DefenseStatus(position = second[it].value)
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            Status(
                value = playerDetail.player.meet,
                alphabet = playerDetail.player.meet.statusAlphabet(),
                color = playerDetail.player.meet.statusColor(),
                modifier = Modifier.fillMaxWidth()
            )
            Status(
                value = playerDetail.player.power,
                alphabet = playerDetail.player.power.statusAlphabet(),
                color = playerDetail.player.power.statusColor(),
                modifier = Modifier.fillMaxWidth()
            )
            Status(
                value = playerDetail.player.speed,
                alphabet = playerDetail.player.speed.statusAlphabet(),
                color = playerDetail.player.speed.statusColor(),
                modifier = Modifier.fillMaxWidth()
            )
            Status(
                value = playerDetail.player.throwing,
                alphabet = playerDetail.player.throwing.statusAlphabet(),
                color = playerDetail.player.throwing.statusColor(),
                modifier = Modifier.fillMaxWidth()
            )
            Status(
                value = playerDetail.player.defense,
                alphabet = playerDetail.player.defense.statusAlphabet(),
                color = playerDetail.player.defense.statusColor(),
                modifier = Modifier.fillMaxWidth()
            )
            Status(
                value = playerDetail.player.catching,
                alphabet = playerDetail.player.catching.statusAlphabet(),
                color = playerDetail.player.catching.statusColor(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DefenseStatus(
    position: PlayerPosition,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = position.position.toShortJa(),
            fontSize = 20.sp,
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
        Text(
            text = position.defense.statusAlphabet(),
            fontSize = 24.sp,
            color = position.defense.statusColor(),
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FielderScreenPreview() {
    FielderContent(
        uiState = CommandUiState(),
        onPlayerClick = { },
        orderType = OrderType.NORMAL,
    )
}

@Preview(showBackground = true)
@Composable
fun OrderListPreview() {
    OrderList(
        fielders = listOf(
            DisplayFielder(0, "Pitcher", "投", 1, true, pitcherColor),
            DisplayFielder(1, "Player 2", "捕", 2, true, catcherColor),
            DisplayFielder(2, "Player 3", "一", 3, true, infielderColor),
            DisplayFielder(3, "Player 4", "二", 4, true, infielderColor),
            DisplayFielder(4, "Player 5", "三", 5, true, infielderColor),
            DisplayFielder(5, "Player 6", "遊", 6, true, infielderColor),
            DisplayFielder(6, "Player 7", "左", 7, true, outfielderColor),
            DisplayFielder(7, "Player 8", "中", 8, true, outfielderColor),
            DisplayFielder(8, "Player 9", "右", 9, true, outfielderColor),
        ),
        onItemClick = { },
    )
}

@Preview(showBackground = true)
@Composable
fun BenchListPreview() {
    BenchList(
        fielders = listOf(
            DisplayFielder(0, "Player 10", "捕", 10, true, catcherColor),
            DisplayFielder(0, "Player 11", "一", 11, true, infielderColor),
            DisplayFielder(0, "Player 12", "外", 12, true, outfielderColor),
            DisplayFielder(0, "Player 13", "三", 13, true, infielderColor),
            DisplayFielder(0, "Player 14", "外", 14, true, infielderColor),
            DisplayFielder(0, "Player 15", "遊", 15, true, infielderColor),
        ),
        onItemClick = { }
    )
}

@Preview(showBackground = true)
@Composable
fun SubstituteListPreview() {
    SubstituteList(
        fielders = listOf(
            DisplayFielder(0, "Player 16", "捕", 16, false, catcherColor),
            DisplayFielder(0, "Player 17", "一", 17, false, infielderColor),
            DisplayFielder(0, "Player 18", "外", 18, false, outfielderColor),
            DisplayFielder(0, "Player 19", "三", 19, false, infielderColor),
            DisplayFielder(0, "Player 20", "外", 20, false, outfielderColor),
            DisplayFielder(0, "Player 21", "遊", 21, false, infielderColor),
            DisplayFielder(0, "Player 22", "二", 22, false, infielderColor),
        ),
        onItemClick = { }
    )
}

@Preview(showBackground = true)
@Composable
fun PlayerDetailPreview() {
    FielderDetail(
        playerDetail = DisplayPlayerDetail(
            player = Player(
                id = 1,
                firstName = "山田",
                lastName = "太郎",
                teamId = 0,
                meet = 72,
                power = 51,
                speed = 68,
                throwing = 58,
                defense = 62,
                catching = 66,
                ballSpeed = 120,
                control = 1,
                stamina = 1
            ),
            positions = listOf(
                PlayerPosition(1, Position.OUTFIELDER, 62),
                PlayerPosition(1, Position.FIRST_BASEMAN, 58)
            ),
            color = outfielderColor
        )
    )
}