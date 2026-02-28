package com.example.penasim.usecase

import com.example.penasim.domain.GameFixture
import com.example.penasim.domain.GameResult
import com.example.penasim.domain.League
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.GameResultRepository
import com.example.penasim.domain.repository.TeamRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import kotlin.test.assertFailsWith

class ExecuteGameUseCaseTest {

    private val gameResultRepository: GameResultRepository = mock()
    private val gameFixtureRepository: GameFixtureRepository = mock()
    private val teamRepository: TeamRepository = mock()

    private val useCase = ExecuteGameUseCase(
        gameResultRepository,
        gameFixtureRepository,
        teamRepository,
    )

    private val league = League.L1
    private val home = Team(1, "Home", league)
    private val away = Team(2, "Away", league)
    private val date: LocalDate = LocalDate.of(2025, 8, 1)
    private val fixture = GameFixture(10, date, 0, home.id, away.id)

    @Test
    fun execute_returnsGameInfo_whenAllDataExists() = runTest {
        val result = GameResult(10, 5, 2)
        whenever(gameResultRepository.createGame(10, 5, 2)).thenReturn(result)
        whenever(gameFixtureRepository.getGameFixture(10)).thenReturn(fixture)
        whenever(teamRepository.getTeam(home.id)).thenReturn(home)
        whenever(teamRepository.getTeam(away.id)).thenReturn(away)

        val info = useCase.execute(10, 5, 2)

        assertEquals(fixture, info.fixture)
        assertEquals(home, info.homeTeam)
        assertEquals(away, info.awayTeam)
        assertEquals(result, info.result)
    }

    @Test
    fun execute_throws_whenFixtureAlreadyUsed() = runTest {
        whenever(gameResultRepository.createGame(10, 1, 1)).thenReturn(null)

        assertFailsWith<IllegalArgumentException> {
            useCase.execute(10, 1, 1)
        }
    }

    @Test
    fun execute_throws_whenFixtureMissing() = runTest {
        whenever(gameResultRepository.createGame(10, 1, 1)).thenReturn(GameResult(10, 1, 1))
        whenever(gameFixtureRepository.getGameFixture(10)).thenReturn(null)

        assertFailsWith<IllegalArgumentException> {
            useCase.execute(10, 1, 1)
        }
    }

    @Test
    fun execute_throws_whenHomeTeamMissing() = runTest {
        whenever(gameResultRepository.createGame(10, 1, 1)).thenReturn(GameResult(10, 1, 1))
        whenever(gameFixtureRepository.getGameFixture(10)).thenReturn(fixture)
        whenever(teamRepository.getTeam(home.id)).thenReturn(null)

        assertFailsWith<IllegalArgumentException> {
            useCase.execute(10, 1, 1)
        }
    }

    @Test
    fun execute_throws_whenAwayTeamMissing() = runTest {
        whenever(gameResultRepository.createGame(10, 1, 1)).thenReturn(GameResult(10, 1, 1))
        whenever(gameFixtureRepository.getGameFixture(10)).thenReturn(fixture)
        whenever(teamRepository.getTeam(home.id)).thenReturn(home)
        whenever(teamRepository.getTeam(away.id)).thenReturn(null)

        assertFailsWith<IllegalArgumentException> {
            useCase.execute(10, 1, 1)
        }
    }
}
