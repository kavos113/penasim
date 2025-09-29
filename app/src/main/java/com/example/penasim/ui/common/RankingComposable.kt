package com.example.penasim.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.penasim.domain.toLeague

@Composable
fun Ranking(
    rankings: List<RankingUiInfo>,
    modifier: Modifier = Modifier
) {
    assert(rankings.size == 12)
    Column(
        modifier = modifier
    ) {
        repeat(2) {
            LeagueRanking(
                rankings = rankings.filter { ranking -> ranking.league == it.toLeague() },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun LeagueRanking(
    rankings: List<RankingUiInfo>,
    modifier: Modifier = Modifier
) {
    assert(rankings.size == 6)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(6) {
            RankingTeam(
                teamLogo = rankings[it].teamIcon,
                gamesBack = rankings[it].gameBack,
                isMyTeam = rankings[it].isMyTeam,
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun RankingTeam(
    @DrawableRes teamLogo: Int,
    gamesBack: Double,
    isMyTeam: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .then(
                if (isMyTeam) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small
                    )
                } else {
                    Modifier
                }
            )
            .padding(4.dp)
    ) {
        Image(
            painter = painterResource(id = teamLogo),
            contentDescription = "team icon",
            modifier = Modifier
                .size(18.dp)
                .align(Alignment.CenterVertically)
        )
        Text(
            text = gamesBack.toString(),
            modifier = Modifier
        )
    }
}