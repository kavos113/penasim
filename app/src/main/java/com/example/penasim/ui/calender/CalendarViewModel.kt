package com.example.penasim.ui.calender

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.penasim.model.PennantManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalendarViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private val pennantManager = PennantManager()

    fun nextGame() {

        val currentGame = pennantManager.nextRandomGame()

        val recentGames = pennantManager.getRecentGames()

        _uiState.update { currentState ->
            val newGames = currentState.games.toMutableList()
            newGames[currentGame - 1] = recentGames.map { gameInfo ->
                GameUiInfo(
                    day = gameInfo.day,
                    homeTeamIcon = pennantManager.teamInfo[gameInfo.homeTeamId].teamIcon,
                    awayTeamIcon = pennantManager.teamInfo[gameInfo.awayTeamId].teamIcon,
                    homeTeamScore = gameInfo.homeTeamScore,
                    awayTeamScore = gameInfo.awayTeamScore,
                    isGameFinished = true
                )
            }
            currentState.copy(
                games = newGames,
                rankings = pennantManager.teamInfo.map { teamInfo ->
                    RankingUiInfo(
                        league = teamInfo.league,
                        rank = teamInfo.rank,
                        teamIcon = teamInfo.teamIcon,
                        gameBack = teamInfo.gameBack
                    )
                }.sortedBy { it.rank },
                currentDay = currentGame
            )
        }

        Log.d("CalendarViewModel", "Current Game: $currentGame ======================")
        val league1 = pennantManager.teamInfo.filter { it.league == 0 }
        league1.forEach {
            Log.d(
                "CalendarViewModel",
                "Team: ${it.teamName} Wins: ${it.wins} Losses: ${it.losses} Draws: ${it.draws} Rank: ${it.rank} Game Back: ${it.gameBack}"
            )
        }
    }
}