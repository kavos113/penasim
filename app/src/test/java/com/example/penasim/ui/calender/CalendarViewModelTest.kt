package com.example.penasim.ui.calender

import com.example.penasim.const.Constants
import com.example.penasim.core.session.InMemorySelectedTeamStore
import com.example.penasim.features.game.application.ExecuteGamesByDate
import com.example.penasim.features.game.usecase.CurrentDayUseCase
import com.example.penasim.features.game.usecase.GameInfoUseCase
import com.example.penasim.features.schedule.ui.calender.CalendarViewModel
import com.example.penasim.features.schedule.usecase.GameScheduleUseCase
import com.example.penasim.features.standing.usecase.RankingUseCase
import com.example.penasim.testing.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class CalendarViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val selectedTeamStore = InMemorySelectedTeamStore()
    private val gameScheduleUseCase: GameScheduleUseCase = mock()
    private val gameInfoUseCase: GameInfoUseCase = mock()
    private val currentDayUseCase: CurrentDayUseCase = mock()
    private val rankingUseCase: RankingUseCase = mock()
    private val executeGamesByDate: ExecuteGamesByDate = mock()

    private suspend fun buildViewModel(): CalendarViewModel {
        whenever(gameScheduleUseCase.getAll()).thenReturn(emptyList())
        whenever(gameInfoUseCase.getAll()).thenReturn(emptyList())
        whenever(currentDayUseCase.getCurrentDay()).thenReturn(Constants.START)
        whenever(rankingUseCase.getByLeague(any())).thenReturn(emptyList())
        return CalendarViewModel(
            selectedTeamStore,
            gameScheduleUseCase,
            gameInfoUseCase,
            currentDayUseCase,
            rankingUseCase,
            executeGamesByDate
        )
    }

    @Test
    fun init_buildsEmptyGames_whenNoSchedulesOrResults() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()

        assertEquals(Constants.START, vm.uiState.value.currentDay)
        assertTrue(vm.uiState.value.games.containsKey(Constants.START))
        assertTrue(vm.uiState.value.games[Constants.START]?.isEmpty() == true)
        assertTrue(vm.uiState.value.rankings.isEmpty())
    }

    @Test
    fun nextGame_updatesGamesForCurrentDay_withEmptyRecentGames() = runTest {
        whenever(executeGamesByDate.execute(any())).thenReturn(emptyList())
        val vm = buildViewModel()
        advanceUntilIdle()

        val day = vm.uiState.value.currentDay
        vm.nextGame()
        advanceUntilIdle()

        assertTrue(vm.uiState.value.games.containsKey(day))
        assertTrue(vm.uiState.value.games[day]?.isEmpty() == true)
    }
}
