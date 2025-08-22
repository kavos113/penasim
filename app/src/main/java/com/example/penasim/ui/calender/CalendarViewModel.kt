package com.example.penasim.ui.calender

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.penasim.model.PennantManager
import com.example.penasim.data.PennantDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private val db = PennantDatabase.getDatabase(application.applicationContext)
    private val pennantManager = PennantManager(
        gameMasterDao = db.gameMasterDao()
    )

    private var totalDay = 1;

    init {
        viewModelScope.launch {
            val initialGames = pennantManager.getInitialData()
            _uiState.update { currentState ->
                currentState.copy(
                    games = initialGames.map { dayGames ->
                        dayGames.map { gameInfo ->
                            GameUiInfo(
                                day = gameInfo.day,
                                homeTeamIcon = pennantManager.teamInfo[gameInfo.homeTeamId].teamIcon,
                                awayTeamIcon = pennantManager.teamInfo[gameInfo.awayTeamId].teamIcon,
                                homeTeamScore = gameInfo.homeTeamScore,
                                awayTeamScore = gameInfo.awayTeamScore,
                                isGameFinished = false
                            )
                        }
                    },
                    rankings = currentState.rankings,
                    currentDay = currentState.currentDay
                )
            }
            Log.d("CalendarViewModel", "Initial data loaded, total days: ${initialGames.size}")
        }
    }

    fun nextGame() {
        if (totalDay > 181){
            return
        }
        viewModelScope.launch {
            val recentGames = pennantManager.nextGameFromDB(totalDay)

            _uiState.update { currentState ->
                val newGames = currentState.games.toMutableList()
                newGames[totalDay - 1] = recentGames.map { gameInfo ->
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
                    currentDay = totalDay
                )
            }

            Log.d("CalendarViewModel", "Current Game: $totalDay ======================")
            val league1 = pennantManager.teamInfo.filter { it.league == 0 }
            league1.forEach {
                Log.d(
                    "CalendarViewModel",
                    "Team: ${it.teamName} Wins: ${it.wins} Losses: ${it.losses} Draws: ${it.draws} Rank: ${it.rank} Game Back: ${it.gameBack}"
                )
            }

            totalDay++
        }
    }
}