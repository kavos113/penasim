package com.example.penasim.ui.calender

import com.example.penasim.const.Constants
import com.example.penasim.domain.League
import com.example.penasim.game.ExecuteGamesByDate
import com.example.penasim.testing.MainDispatcherRule
import com.example.penasim.usecase.GameInfoUseCase
import com.example.penasim.usecase.GameScheduleUseCase
import com.example.penasim.usecase.RankingUseCase
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

    private val gameScheduleUseCase: GameScheduleUseCase = mock()
    private val gameInfoUseCase: GameInfoUseCase = mock()
    private val rankingUseCase: RankingUseCase = mock()
    private val executeGamesByDate: ExecuteGamesByDate = mock()

    private suspend fun buildViewModel(): CalendarViewModel {
        whenever(gameScheduleUseCase.getAll()).thenReturn(emptyList())
        whenever(gameInfoUseCase.getAll()).thenReturn(emptyList())
        whenever(rankingUseCase.getByLeague(any())).thenReturn(emptyList())
        return CalendarViewModel(gameScheduleUseCase, gameInfoUseCase, rankingUseCase, executeGamesByDate)
    }

    @Test
    fun init_buildsEmptyGames_whenNoSchedulesOrResults() = runTest {
        val vm = buildViewModel()

        // currentDay should be START when there is no game info
        assertEquals(Constants.START, vm.uiState.value.currentDay)
        // games map should contain START with empty list
        assertTrue(vm.uiState.value.games.containsKey(Constants.START))
        assertTrue(vm.uiState.value.games[Constants.START]?.isEmpty() == true)
        // rankings should be empty when mock returns empty
        assertTrue(vm.uiState.value.rankings.isEmpty())
    }

    @Test
    fun nextGame_updatesGamesForCurrentDay_withEmptyRecentGames() = runTest {
        whenever(executeGamesByDate.execute(any())).thenReturn(emptyList())
        val vm = buildViewModel()

        val day = vm.uiState.value.currentDay
        vm.nextGame()

        assertTrue(vm.uiState.value.games.containsKey(day))
        assertTrue(vm.uiState.value.games[day]?.isEmpty() == true)
    }
}
