package com.example.penasim.ui.game

import com.example.penasim.const.Constants
import com.example.penasim.domain.PlayerInfo
import java.time.LocalDate

data class GameUiState(
    val date: LocalDate = Constants.START,
    val homePlayers: List<PlayerInfo> = emptyList(),
    val awayPlayers: List<PlayerInfo> = emptyList(),
    val beforeGameInfo: BeforeGameInfo = BeforeGameInfo(),
    val afterGameInfo: AfterGameInfo = AfterGameInfo(),
)
