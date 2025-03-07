package com.example.penasim.data

import com.example.penasim.R
import com.example.penasim.ui.calender.GameUiInfo
import com.example.penasim.ui.calender.RankingUiInfo

object DataSource {
    val rankings = List(12) {
        RankingUiInfo(
            league = it % 2,
            rank = it / 2,
            teamIcon = when (it) {
                0 -> R.drawable.team1_icon
                1 -> R.drawable.team2_icon
                2 -> R.drawable.team3_icon
                3 -> R.drawable.team4_icon
                4 -> R.drawable.team5_icon
                5 -> R.drawable.team6_icon
                6 -> R.drawable.team7_icon
                7 -> R.drawable.team8_icon
                8 -> R.drawable.team9_icon
                9 -> R.drawable.team10_icon
                10 -> R.drawable.team11_icon
                else -> R.drawable.team12_icon
            },
            gameBack = 0.0
        )
    }

    val games = List(143) { index ->
        List(6) {
            GameUiInfo(
                day = index,
                homeTeamIcon = R.drawable.team1_icon,
                homeTeamScore = 0,
                awayTeamIcon = R.drawable.team1_icon,
                awayTeamScore = 0,
                isGameFinished = false
            )
        }
    }
}