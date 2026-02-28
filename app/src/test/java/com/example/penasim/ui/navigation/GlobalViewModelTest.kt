package com.example.penasim.ui.navigation

import com.example.penasim.const.Constants
import com.example.penasim.domain.GameFixture
import com.example.penasim.domain.GameInfo
import com.example.penasim.domain.GameResult
import com.example.penasim.domain.League
import com.example.penasim.domain.Team
import com.example.penasim.testing.MainDispatcherRule
import com.example.penasim.usecase.GameInfoUseCase
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
    fun init_setsCurrentDay_fromMaxFixtureDatePlusOne() = runTest {
        val home = Team(1, "Home", League.L1)
        val away = Team(2, "Away", League.L1)
        val d1 = LocalDate.of(2025, 7, 1)
        val d2 = LocalDate.of(2025, 7, 5)
        val fixture1 = GameFixture(10, d1, 1, home.id, away.id)
        val fixture2 = GameFixture(11, d2, 1, home.id, away.id)

        val gameInfoUseCase: GameInfoUseCase = mock()
        whenever(gameInfoUseCase.getAll()).thenReturn(
            listOf(
                GameInfo(fixture = fixture1, homeTeam = home, awayTeam = away, result = GameResult(10, 1, 0)),
                GameInfo(fixture = fixture2, homeTeam = home, awayTeam = away, result = GameResult(11, 2, 3))
            )
        )

        val vm = GlobalViewModel(gameInfoUseCase)

        val expected = d2.plusDays(1)
        assertEquals(expected, vm.state.value.currentDay)
    }

    @Test
    fun nextDay_incrementsCurrentDay() = runTest {
        val gameInfoUseCase: GameInfoUseCase = mock()
        whenever(gameInfoUseCase.getAll()).thenReturn(emptyList())

        val vm = GlobalViewModel(gameInfoUseCase)

        val initial = vm.state.value.currentDay
        assertEquals(Constants.START, initial)

        vm.nextDay()

        assertEquals(initial.plusDays(1), vm.state.value.currentDay)
    }
}

