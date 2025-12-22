package com.example.penasim.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.penasim.domain.Player
import com.example.penasim.domain.PlayerPosition
import com.example.penasim.domain.Position
import com.example.penasim.domain.TotalBattingStats
import com.example.penasim.domain.TotalPitchingStats
import com.example.penasim.domain.toShortJa
import com.example.penasim.ui.theme.outfielderColor
import com.example.penasim.ui.theme.pitcherColor
import com.example.penasim.ui.theme.playerBorderColor


@Composable
internal fun OrderPlayerItem(
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
            SpacedText(
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
                .background(color = player.position.color())
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = player.position.toShortJa(),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
internal fun SimplePlayerItem(
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
            .then(modifier)
    ) {
        SpacedText(
            text = displayName,
            fontSize = 16.sp,
        )
    }
}

@Composable
internal fun Status(
    value: Int,
    alphabet: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = alphabet,
                fontSize = 16.sp,
                color = color
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = value.toString(),
                fontSize = 16.sp,
            )
        }
    }
}

@Composable
private fun SpacedText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
) {
    val spacing = when(text.length) {
        2 -> 1.0.em
        3 -> 0.5.em
        4 -> 0.25.em
        else -> 0.0.em
    }

    Text(
        text = text,
        letterSpacing = spacing,
        fontSize = fontSize,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Composable
internal fun FielderDetail(
    playerDetail: DisplayPlayerDetail,
    modifier: Modifier = Modifier
) {
    Row {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .weight(2f)
                .padding(6.dp)
        ) {
            SimplePlayerItem(
                displayName = playerDetail.player.firstName,
                color = playerDetail.color,
            )
            Text(
                text = playerDetail.battingStats.battingAverageString,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "${playerDetail.battingStats.homeRun}本 ${playerDetail.battingStats.rbi}点",
                fontSize = 16.sp,
            )
            Text(
                text = "${playerDetail.battingStats.rbi}盗",
                fontSize = 16.sp,
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
                .padding(6.dp)
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
            fontSize = 16.sp,
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
        Text(
            text = position.defense.statusAlphabet(),
            fontSize = 20.sp,
            color = position.defense.statusColor(),
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

private fun pitcherTypeAppropriate(appropriate: Int): String = when (appropriate) {
    0 -> "△"
    1 -> "〇"
    2 -> "◎"
    else -> "-"
}

@Composable
internal fun PitcherDetail(
    playerDetail: DisplayPlayerDetail,
    modifier: Modifier = Modifier
) {
    Row {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .weight(1f)
                .padding(6.dp)
        ) {
            SimplePlayerItem(
                displayName = playerDetail.player.firstName,
                color = playerDetail.color,
            )
            Text(
                text = playerDetail.pitchingStats.eraStr,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "${playerDetail.pitchingStats.wins}勝${playerDetail.pitchingStats.losses}敗",
                fontSize = 16.sp,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(6.dp)
        ) {
            Text(
                text = "${playerDetail.player.ballSpeed}km/h",
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Status(
                value = playerDetail.player.control,
                alphabet = playerDetail.player.control.statusAlphabet(),
                color = playerDetail.player.control.statusColor(),
                modifier = Modifier.fillMaxWidth()
            )
            Status(
                value = playerDetail.player.stamina,
                alphabet = playerDetail.player.stamina.statusAlphabet(),
                color = playerDetail.player.stamina.statusColor(),
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "先発 ${pitcherTypeAppropriate(playerDetail.player.starter)}",
                fontSize = 16.sp,
            )
            Text(
                text = "救援 ${pitcherTypeAppropriate(playerDetail.player.reliever)}",
                fontSize = 16.sp,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayerDetailPreview() {
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
                stamina = 1,
                starter = 0,
                reliever = 0,
            ),
            positions = listOf(
                PlayerPosition(1, Position.OUTFIELDER, 62),
                PlayerPosition(1, Position.FIRST_BASEMAN, 58)
            ),
            battingStats = TotalBattingStats(
                playerId = 1,
                atBat = 300,
                hit = 102,
                doubleHit = 20,
                tripleHit = 5,
                homeRun = 12,
                walk = 40,
                rbi = 55,
                strikeOut = 60,
            ),
            pitchingStats = TotalPitchingStats(playerId = 1),
            color = outfielderColor
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun PitcherDetailPreview() {
    PitcherDetail(
        playerDetail = DisplayPlayerDetail(
            player = Player(
                id = 1,
                firstName = "山田",
                lastName = "太郎",
                teamId = 0,
                meet = 15,
                power = 22,
                speed = 55,
                throwing = 72,
                defense = 51,
                catching = 44,
                ballSpeed = 148,
                control = 61,
                stamina = 70,
                starter = 0,
                reliever = 0,
            ),
            positions = listOf(
                PlayerPosition(1, Position.PITCHER, 51)
            ),
            battingStats = TotalBattingStats(playerId = 1),
            pitchingStats = TotalPitchingStats(
                playerId = 1,
                inningsPitched = 423,
                hits = 105,
                runs = 32,
                earnedRuns = 30,
                walks = 28,
                strikeOuts = 145,
                homeRuns = 4,
                wins = 14,
                losses = 3,
                holds = 0,
                saves = 0,
            ),
            color = pitcherColor
        )
    )
}