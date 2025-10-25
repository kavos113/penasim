package com.example.penasim.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penasim.domain.League
import com.example.penasim.usecase.GameScheduleUseCase
import com.example.penasim.usecase.GetRankingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRankingUseCase: GetRankingUseCase,
    private val gameScheduleUseCase: GameScheduleUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun setCurrentDay(day: LocalDate) {
        _uiState.value = _uiState.value.copy(
            currentDay = day
        )
    }

    fun setTeamId(teamId: Int) {
        _uiState.value = _uiState.value.copy(
            teamId = teamId
        )
    }

    init {
        update()
    }

    fun update() {
        viewModelScope.launch {
            val rankings = (getRankingUseCase.execute(League.L1) + getRankingUseCase.execute(League.L2))
            val rank = rankings.find { it.team.id == _uiState.value.teamId }?.rank ?: 0

            val schedules = gameScheduleUseCase.getByDate(_uiState.value.currentDay)
            val isGameDay = schedules.any { it.homeTeam.id == _uiState.value.teamId || it.awayTeam.id == _uiState.value.teamId }

            _uiState.value = _uiState.value.copy(
                rank = rank,
                isGameDay = isGameDay
            )
        }
    }
}