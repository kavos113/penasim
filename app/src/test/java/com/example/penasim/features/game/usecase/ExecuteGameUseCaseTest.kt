package com.example.penasim.features.game.usecase

import com.example.penasim.features.game.domain.GameResult
import com.example.penasim.features.game.domain.repository.GameResultRepository
import com.example.penasim.features.schedule.domain.GameFixture
import com.example.penasim.features.schedule.domain.repository.GameFixtureRepository
import com.example.penasim.features.team.domain.League
import com.example.penasim.features.team.domain.Team
import com.example.penasim.features.team.domain.repository.TeamRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ExecuteGameUseCaseTest {
  private val gameResultRepository: GameResultRepository = mock()
  private val gameFixtureRepository: GameFixtureRepository = mock()
  private val teamRepository: TeamRepository = mock()

  @Test
  fun execute_returnsGameInfo_whenAllDependenciesExist() = runTest {
    val fixture = GameFixture(
      id = 1,
      date = java.time.LocalDate.of(2025, 3, 28),
      numberOfGames = 1,
      homeTeamId = 10,
      awayTeamId = 11
    )
    val result = GameResult(fixtureId = 1, homeScore = 3, awayScore = 2)
    val homeTeam = Team(id = 10, name = "Home", league = League.L1)
    val awayTeam = Team(id = 11, name = "Away", league = League.L1)

    whenever(gameResultRepository.createGame(1, 3, 2)).thenReturn(result)
    whenever(gameFixtureRepository.getGameFixture(1)).thenReturn(fixture)
    whenever(teamRepository.getTeam(10)).thenReturn(homeTeam)
    whenever(teamRepository.getTeam(11)).thenReturn(awayTeam)

    val actual = ExecuteGameUseCase(
      gameResultRepository = gameResultRepository,
      gameFixtureRepository = gameFixtureRepository,
      teamRepository = teamRepository
    ).execute(fixtureId = 1, homeScore = 3, awayScore = 2)

    assertEquals(fixture, actual.fixture)
    assertEquals(homeTeam, actual.homeTeam)
    assertEquals(awayTeam, actual.awayTeam)
    assertEquals(result, actual.result)
  }

  @Test
  fun execute_throws_whenGameAlreadyExists() = runTest {
    whenever(gameResultRepository.createGame(1, 3, 2)).thenReturn(null)

    assertFailsWith<IllegalArgumentException> {
      ExecuteGameUseCase(gameResultRepository, gameFixtureRepository, teamRepository)
        .execute(fixtureId = 1, homeScore = 3, awayScore = 2)
    }
  }

  @Test
  fun execute_throws_whenFixtureDoesNotExist() = runTest {
    whenever(gameResultRepository.createGame(1, 3, 2)).thenReturn(GameResult(1, 3, 2))
    whenever(gameFixtureRepository.getGameFixture(1)).thenReturn(null)

    assertFailsWith<IllegalArgumentException> {
      ExecuteGameUseCase(gameResultRepository, gameFixtureRepository, teamRepository)
        .execute(fixtureId = 1, homeScore = 3, awayScore = 2)
    }
  }

  @Test
  fun execute_throws_whenHomeTeamDoesNotExist() = runTest {
    val fixture = GameFixture(1, java.time.LocalDate.of(2025, 3, 28), 1, 10, 11)

    whenever(gameResultRepository.createGame(1, 3, 2)).thenReturn(GameResult(1, 3, 2))
    whenever(gameFixtureRepository.getGameFixture(1)).thenReturn(fixture)
    whenever(teamRepository.getTeam(10)).thenReturn(null)

    assertFailsWith<IllegalArgumentException> {
      ExecuteGameUseCase(gameResultRepository, gameFixtureRepository, teamRepository)
        .execute(fixtureId = 1, homeScore = 3, awayScore = 2)
    }
  }

  @Test
  fun execute_throws_whenAwayTeamDoesNotExist() = runTest {
    val fixture = GameFixture(1, java.time.LocalDate.of(2025, 3, 28), 1, 10, 11)
    val homeTeam = Team(id = 10, name = "Home", league = League.L1)

    whenever(gameResultRepository.createGame(1, 3, 2)).thenReturn(GameResult(1, 3, 2))
    whenever(gameFixtureRepository.getGameFixture(1)).thenReturn(fixture)
    whenever(teamRepository.getTeam(10)).thenReturn(homeTeam)
    whenever(teamRepository.getTeam(11)).thenReturn(null)

    assertFailsWith<IllegalArgumentException> {
      ExecuteGameUseCase(gameResultRepository, gameFixtureRepository, teamRepository)
        .execute(fixtureId = 1, homeScore = 3, awayScore = 2)
    }
  }
}
