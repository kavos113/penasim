package com.example.penasim.ui.command

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.penasim.R
import com.example.penasim.domain.PitcherType
import com.example.penasim.ui.navigation.NavigationDestination
import com.example.penasim.ui.theme.pitcherColor

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
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        
    }
}

@Composable
private fun StartingList(
    pitchers: List<DisplayPitcher>,
    modifier: Modifier = Modifier
) {
    Column {
        Text(
            text = stringResource(R.string.starter)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            val (first, second) = pitchers.partition { it.number % 2 == 1 }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                repeat(first.size) { index ->
                    SimplePlayerItem(
                        displayName = first[index].displayName,
                        color = pitcherColor
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                repeat(second.size) { index ->
                    SimplePlayerItem(
                        displayName = second[index].displayName,
                        color = pitcherColor
                    )
                }
            }
        }
    }
}

@Composable
private fun ReliefList(
    pitchers: List<DisplayPitcher>,
    modifier: Modifier = Modifier
) {
    Column {
        Text(
            text = stringResource(R.string.reliever)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            val (first, second) = pitchers.partition { it.number % 2 == 1 }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                repeat(first.size) { index ->
                    SimplePlayerItem(
                        displayName = first[index].displayName,
                        color = pitcherColor
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                repeat(second.size) { index ->
                    SimplePlayerItem(
                        displayName = second[index].displayName,
                        color = pitcherColor
                    )
                }
            }
        }
    }
}

@Composable
private fun CloseList(
    pitcher: DisplayPitcher,
    modifier: Modifier = Modifier
) {
    Column {
        Text(
            text = stringResource(R.string.closer)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
                .fillMaxWidth(0.5f)
                .padding(4.dp)
        ) {
            SimplePlayerItem(
                displayName = pitcher.displayName,
                color = pitcherColor,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SubstituteList(
    pitchers: List<DisplayPitcher>,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.substitute)
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(4.dp)
        ) {
            items(pitchers) { pitcher ->
                SimplePlayerItem(
                    displayName = pitcher.displayName,
                    color = pitcherColor,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
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
        pitcher = DisplayPitcher(
            id = 12,
            displayName = "吉田",
            type = PitcherType.CLOSER,
            number = 1,
            isMain = true,
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SubstitutePitcherListPreview() {
    SubstituteList(
        pitchers = listOf(
            DisplayPitcher(
                id = 13,
                displayName = "山崎",
                type = PitcherType.STARTER,
                number = 1,
                isMain = false,
            ),
            DisplayPitcher(
                id = 14,
                displayName = "藤田",
                type = PitcherType.RELIEVER,
                number = 2,
                isMain = false,
            ),
            DisplayPitcher(
                id = 15,
                displayName = "松本",
                type = PitcherType.STARTER,
                number = 3,
                isMain = false,
            ),
            DisplayPitcher(
                id = 16,
                displayName = "井上",
                type = PitcherType.RELIEVER,
                number = 4,
                isMain = false,
            )
        )
    )
}