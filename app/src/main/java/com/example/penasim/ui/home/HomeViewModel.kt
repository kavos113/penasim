package com.example.penasim.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penasim.domain.League
import com.example.penasim.usecase.GetGameInfoAllUseCase
import com.example.penasim.usecase.GetRankingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRankingUseCase: GetRankingUseCase,
    private val getGameInfoAllUseCase: GetGameInfoAllUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        update()
    }

    fun update() {
        viewModelScope.launch {
            val rankings = (getRankingUseCase.execute(League.L1) + getRankingUseCase.execute(League.L2))
            val rank = rankings.find { it.team.id == _uiState.value.teamId }?.rank ?: 0

            val gameInfos = getGameInfoAllUseCase.execute()
            val currentDay = gameInfos.maxOfOrNull { it.fixture.date }?.plusDays(1)
                ?: _uiState.value.currentDay

            _uiState.value = _uiState.value.copy(
                rank = rank,
                currentDay = currentDay
            )
        }
    }
}