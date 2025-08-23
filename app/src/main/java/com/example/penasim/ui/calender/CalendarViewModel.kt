package com.example.penasim.ui.calender

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penasim.const.DateConst
import com.example.penasim.domain.League
import com.example.penasim.usecase.ExecuteGamesByDateUseCase
import com.example.penasim.usecase.GetGameSchedulesAllUseCase
import com.example.penasim.usecase.GetRankingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getGameSchedulesAllUseCase: GetGameSchedulesAllUseCase,
    private val getRankingUseCase: GetRankingUseCase,
    private val executeGamesByDateUseCase: ExecuteGamesByDateUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private var currentDate = DateConst.START

    init {
        viewModelScope.launch {
            val gameSchedules = getGameSchedulesAllUseCase.execute()
            val map = gameSchedules.groupBy { it.fixture.date }
            val clauses: Map<LocalDate, List<GameUiInfo>>
                = generateSequence(DateConst.START) { it.plusDays(1) }
                    .takeWhile { !it.isAfter(DateConst.END) }
                    .associateWith { date ->
                        map[date]?.map { it.toGameUiInfo() } ?: emptyList()
                    }

            _uiState.update { currentState ->
                currentState.copy(
                    games = clauses,
                    rankings = currentState.rankings,
                    currentDay = currentState.currentDay
                )
            }
            Log.d("CalendarViewModel", "Initial data loaded, total days: ${clauses.size}")
        }
    }

    fun nextGame() {
        if (currentDate >= DateConst.END) {
            Log.d("CalendarViewModel", "All games have been processed.")
            return
        }

        viewModelScope.launch {
            val recentGames = executeGamesByDateUseCase.execute(currentDate)

            _uiState.update { currentState ->
                val newGames = currentState.games.toMutableMap()
                newGames[currentDate] = recentGames.map { it.toGameUiInfo() }

                val rankings = (getRankingUseCase.execute(League.L1) + getRankingUseCase.execute(League.L2))
                    .sortedBy { it.rank }
                    .map { it.toRankingUiInfo() }

                currentState.copy(
                    games = newGames,
                    rankings = rankings,
                    currentDay = currentDate
                )
            }

            Log.d("CalendarViewModel", "Current Game: $currentDate ======================")
            val league1Rankings = getRankingUseCase.execute(League.L1)
            league1Rankings.forEach {
                Log.d("CalendarViewModel", "L1 Ranking - Rank: ${it.rank}, Team: ${it.team.name}, Wins: ${it.wins}, Losses: ${it.losses}, GB: ${"%.1f".format(it.gameBack)}")
            }
        }
    }
}