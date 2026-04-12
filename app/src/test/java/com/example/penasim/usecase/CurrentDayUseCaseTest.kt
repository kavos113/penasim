package com.example.penasim.usecase

import com.example.penasim.const.Constants
import com.example.penasim.features.game.domain.GameInfo
import com.example.penasim.features.game.domain.GameResult
import com.example.penasim.features.game.usecase.CurrentDayUseCase
import com.example.penasim.features.game.usecase.GameInfoUseCase
import com.example.penasim.features.schedule.domain.GameFixture
import com.example.penasim.features.team.domain.League
import com.example.penasim.features.team.domain.Team
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import kotlin.test.assertEquals

class CurrentDayUseCaseTest {

  @Test
  fun getCurrentDay_returnsNextDayOfLatestFixture() = runTest {
    val gameInfoUseCase: GameInfoUseCase = mock()
    val home = Team(1, "Home", League.L1)
    val away = Team(2, "Away", League.L1)
    whenever(gameInfoUseCase.getAll()).thenReturn(
      listOf(
        GameInfo(GameFixture(1, LocalDate.of(2025, 7, 1), 1, 1, 2), home, away, GameResult(1, 1, 0)),
        GameInfo(GameFixture(2, LocalDate.of(2025, 7, 5), 2, 1, 2), home, away, GameResult(2, 2, 1)),
      )
    )

    val actual = CurrentDayUseCase(gameInfoUseCase).getCurrentDay()

    assertEquals(LocalDate.of(2025, 7, 6), actual)
  }

  @Test
  fun getCurrentDay_returnsSeasonStart_whenNoGamesExist() = runTest {
    val gameInfoUseCase: GameInfoUseCase = mock()
    whenever(gameInfoUseCase.getAll()).thenReturn(emptyList())

    val actual = CurrentDayUseCase(gameInfoUseCase).getCurrentDay()

    assertEquals(Constants.START, actual)
  }
}
