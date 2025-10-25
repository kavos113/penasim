package com.example.penasim.ui.calender

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penasim.const.Constants
import com.example.penasim.domain.League
import com.example.penasim.game.ExecuteGamesByDate
import com.example.penasim.ui.common.GameUiInfo
import com.example.penasim.ui.common.toGameUiInfo
import com.example.penasim.ui.common.toGameUiInfoWithResult
import com.example.penasim.ui.common.toRankingUiInfo
import com.example.penasim.usecase.GameInfoUseCase
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
    private val gameInfoUseCase: GameInfoUseCase,
    private val getRankingUseCase: GetRankingUseCase,
    private val executeGamesByDate: ExecuteGamesByDate
) : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun setCurrentDay(day: LocalDate) {
        _uiState.value = _uiState.value.copy(
            currentDay = day
        )
    }

    init {
        viewModelScope.launch {
            val gameSchedules = getGameSchedulesAllUseCase.execute()
            val gameInfos = gameInfoUseCase.getAll()
            val map = gameSchedules.groupBy { it.fixture.date }
            val clauses: Map<LocalDate, List<GameUiInfo>>
                = generateSequence(Constants.START) { it.plusDays(1) }
                    .takeWhile { !it.isAfter(Constants.END) }
                    .associateWith { date ->
                        map[date]?.map {
                            val info = gameInfos.find { info -> info.fixture.id == it.fixture.id }
                            if (info != null) {
                                it.toGameUiInfoWithResult(info)
                            } else {
                                it.toGameUiInfo()
                            }
                        } ?: emptyList()
                    }

            val rankings = (getRankingUseCase.execute(League.L1) + getRankingUseCase.execute(League.L2))
                .sortedBy { it.rank }
                .map { it.toRankingUiInfo() }

            val currentDay = gameInfos.maxOfOrNull { it.fixture.date }?.plusDays(1)
                ?: Constants.START

            println("Initial currentDay: $currentDay")

            _uiState.update { currentState ->
                currentState.copy(
                    games = clauses,
                    rankings = rankings,
                    currentDay = currentDay
                )
            }
            println("[CalendarViewModel] Initial data loaded, total days: ${clauses.size}")
        }
    }

    fun nextGame() {
        val currentDate = _uiState.value.currentDay
        if (currentDate > Constants.END) {
            println("[CalendarViewModel] All games have been processed.")
            return
        }

        viewModelScope.launch {
            val recentGames = executeGamesByDate.execute(currentDate)

            _uiState.update { currentState ->
                val newGames = currentState.games.toMutableMap()
                newGames[currentDate] = recentGames.map { it.toGameUiInfo() }

                val rankings = (getRankingUseCase.execute(League.L1) + getRankingUseCase.execute(League.L2))
                    .sortedBy { it.rank }
                    .map { it.toRankingUiInfo() }

                currentState.copy(
                    games = newGames,
                    rankings = rankings,
                )
            }

            println("[CalendarViewModel] Current Game: $currentDate ======================")
            val league1Rankings = getRankingUseCase.execute(League.L1)
            league1Rankings.forEach {
                println("[CalendarViewModel] L1 Ranking - Rank: ${it.rank}, Team: ${it.team.name}, Wins: ${it.wins}, Losses: ${it.losses}, GB: ${"%.1f".format(it.gameBack)}")
            }
        }
    }
}