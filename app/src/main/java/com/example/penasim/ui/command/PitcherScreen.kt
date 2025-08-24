package com.example.penasim.ui.command

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.penasim.R
import com.example.penasim.domain.PitcherType
import com.example.penasim.ui.navigation.NavigationDestination

object PitcherDestination : NavigationDestination {
    override val route: String = "pitcher"
    override val titleResId: Int = R.string.pitcher
}

@Composable
fun PitcherScreen(
    modifier: Modifier = Modifier,
    commandViewModel: CommandViewModel
) {
    val uiState by commandViewModel.uiState.collectAsState()

}

@Composable
private fun PitcherContent(
    uiState: CommandUiState,
    modifier: Modifier = Modifier
) {

}

@Composable
private fun StartingList(
    pitchers: List<DisplayPitcher>,
    modifier: Modifier = Modifier
) {

}

@Composable
private fun ReliefList(
    pitchers: List<DisplayPitcher>,
    modifier: Modifier = Modifier
) {

}

@Composable
private fun CloseList(
    pitchers: List<DisplayPitcher>,
    modifier: Modifier = Modifier
) {

}

@Composable
private fun SubstituteList(
    pitchers: List<DisplayPitcher>,
    modifier: Modifier = Modifier
) {

}

@Preview(showBackground = true)
@Composable
fun StartingListPreview() {
    StartingList(
        pitchers = listOf(
            DisplayPitcher(
                id = 1,
                displayName = "山田",
                type = PitcherType.STARTER,
                number = 1,
                isMain = true,
            ),
            DisplayPitcher(
                id = 2,
                displayName = "田中",
                type = PitcherType.STARTER,
                number = 2,
                isMain = true,
            ),
            DisplayPitcher(
                id = 3,
                displayName = "鈴木",
                type = PitcherType.STARTER,
                number = 3,
                isMain = true,
            ),
            DisplayPitcher(
                id = 4,
                displayName = "佐藤",
                type = PitcherType.STARTER,
                number = 4,
                isMain = true,
            ),
            DisplayPitcher(
                id = 5,
                displayName = "高橋",
                type = PitcherType.STARTER,
                number = 5,
                isMain = true,
            ),
        )
    )
}

@Preview(showBackground = true)
@Composable
fun ReliefListPreview() {
    ReliefList(
        pitchers = listOf(
            DisplayPitcher(
                id = 6,
                displayName = "伊藤",
                type = PitcherType.RELIEVER,
                number = 1,
                isMain = true,
            ),
            DisplayPitcher(
                id = 7,
                displayName = "渡辺",
                type = PitcherType.RELIEVER,
                number = 2,
                isMain = true,
            ),
            DisplayPitcher(
                id = 8,
                displayName = "山本",
                type = PitcherType.RELIEVER,
                number = 3,
                isMain = true,
            ),
            DisplayPitcher(
                id = 9,
                displayName = "中村",
                type = PitcherType.RELIEVER,
                number = 4,
                isMain = true,
            ),
            DisplayPitcher(
                id = 10,
                displayName = "小林",
                type = PitcherType.RELIEVER,
                number = 5,
                isMain = true,
            ),
            DisplayPitcher(
                id = 11,
                displayName = "加藤",
                type = PitcherType.RELIEVER,
                number = 6,
                isMain = true,
            ),
        )
    )
}

@Preview(showBackground = true)
@Composable
fun CloseListPreview() {
    CloseList(
        pitchers = listOf(
            DisplayPitcher(
                id = 12,
                displayName = "吉田",
                type = PitcherType.CLOSER,
                number = 1,
                isMain = true,
            ),
        )
    )
}