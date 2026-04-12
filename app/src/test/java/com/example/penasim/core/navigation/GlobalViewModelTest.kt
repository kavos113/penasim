package com.example.penasim.core.navigation

import com.example.penasim.const.Constants
import com.example.penasim.core.navigation.GlobalViewModel
import com.example.penasim.core.session.InMemorySelectedTeamStore
import com.example.penasim.features.game.usecase.CurrentDayUseCase
import com.example.penasim.core.testing.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate

class GlobalViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun init_setsCurrentDay_fromUseCase_andSelectedTeamStore() = runTest {
        val currentDayUseCase: CurrentDayUseCase = mock()
        val selectedTeamStore = InMemorySelectedTeamStore().also { it.setTeamId(3) }
        val expectedDay = LocalDate.of(2025, 7, 6)
        whenever(currentDayUseCase.getCurrentDay()).thenReturn(expectedDay)

        val vm = GlobalViewModel(currentDayUseCase, selectedTeamStore)
        advanceUntilIdle()

        assertEquals(expectedDay, vm.state.value.currentDay)
        assertEquals(3, vm.state.value.teamId)
    }

    @Test
    fun nextDay_incrementsCurrentDay() = runTest {
        val currentDayUseCase: CurrentDayUseCase = mock()
        val selectedTeamStore = InMemorySelectedTeamStore()
        whenever(currentDayUseCase.getCurrentDay()).thenReturn(Constants.START)

        val vm = GlobalViewModel(currentDayUseCase, selectedTeamStore)
        advanceUntilIdle()

        val initial = vm.state.value.currentDay
        vm.nextDay()

        assertEquals(initial.plusDays(1), vm.state.value.currentDay)
    }
}


