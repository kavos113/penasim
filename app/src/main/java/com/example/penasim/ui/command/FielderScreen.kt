package com.example.penasim.ui.command

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.penasim.R
import com.example.penasim.domain.League
import com.example.penasim.domain.Team
import com.example.penasim.ui.navigation.NavigationDestination

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
}

@Composable
private fun FielderContent(
    uiState: CommandUiState,
    modifier: Modifier = Modifier
) {
    Row (
        modifier = modifier
    ) {
        OrderList(
            fielders = uiState.getDisplayFielders(uiState.orderFielderAppointments),
            modifier = Modifier.weight(1f)
        )
        BenchList(
            fielders = uiState.getDisplayFielders(uiState.benchFielderAppointments),
            modifier = Modifier.weight(1f)
        )
        SubstituteList(
            fielders = uiState.getDisplayFielders(uiState.subFielderAppointments),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun OrderList(
    fielders: List<DisplayFielder>,
    modifier: Modifier = Modifier
) {
    Column(
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
        modifier = modifier
    ) {
        Text("ベンチ")
        repeat(fielders.size) {
            SubstitutePlayerItem(displayName = fielders[it].displayName)
        }
    }
}

@Composable
private fun SubstituteList(
    fielders: List<DisplayFielder>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text("2軍")
        repeat(fielders.size) {
            SubstitutePlayerItem(displayName = fielders[it].displayName)
        }
    }
}

@Composable
private fun OrderPlayerItem(
    player: DisplayFielder,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Text(player.number.toString())
        Text(player.displayName)
        Text(player.position)
    }
}

@Composable
private fun SubstitutePlayerItem(
    displayName: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
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
            DisplayFielder("Player 1", "投", 1, true),
            DisplayFielder("Player 2", "捕", 2, true),
            DisplayFielder("Player 3", "一", 3, true),
            DisplayFielder("Player 4", "二", 4, true),
            DisplayFielder("Player 5", "三", 5, true),
            DisplayFielder("Player 6", "遊", 6, true),
            DisplayFielder("Player 7", "左", 7, true),
            DisplayFielder("Player 8", "中", 8, true),
            DisplayFielder("Player 9", "右", 9, true),
        )
    )
}

@Preview(showBackground = true)
@Composable
fun BenchListPreview() {
    BenchList(
        fielders = listOf(
            DisplayFielder("Player 10", "捕", 10, true),
            DisplayFielder("Player 11", "一", 11, true),
            DisplayFielder("Player 12", "外", 12, true),
            DisplayFielder("Player 13", "三", 13, true),
            DisplayFielder("Player 14", "外", 14, true),
            DisplayFielder("Player 15", "遊", 15, true),
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SubstituteListPreview() {
    SubstituteList(
        fielders = listOf(
            DisplayFielder("Player 16", "捕", 16, false),
            DisplayFielder("Player 17", "一", 17, false),
            DisplayFielder("Player 18", "外", 18, false),
            DisplayFielder("Player 19", "三", 19, false),
            DisplayFielder("Player 20", "外", 20, false),
            DisplayFielder("Player 21", "遊", 21, false),
            DisplayFielder("Player 22", "二", 22, false),
        )
    )
}