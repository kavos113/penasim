package com.example.penasim.usecase

import com.example.penasim.domain.*
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.GameResultRepository
import com.example.penasim.domain.repository.TeamRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertFailsWith

class ExecuteGameUseCaseTest {

    private class FakeTeamRepository(private val teams: List<Team>) : TeamRepository {
        override suspend fun getTeam(id: Int): Team? = teams.find { it.id == id }
        override suspend fun getTeamsByLeague(league: League): List<Team> = teams.filter { it.league == league }
        override suspend fun getAllTeams(): List<Team> = teams
    }

    private class FakeGameFixtureRepository(private val fixtures: List<GameFixture>) : GameFixtureRepository {
        override suspend fun getGameFixture(id: Int): GameFixture? = fixtures.find { it.id == id }
        override suspend fun getGameFixturesByDate(date: LocalDate): List<GameFixture> = fixtures.filter { it.date == date }
        override suspend fun getGameFixturesByTeam(team: Team): List<GameFixture> = fixtures.filter { it.homeTeamId == team.id || it.awayTeamId == team.id }
        override suspend fun getAllGameFixtures(): List<GameFixture> = fixtures
    }

    private class FakeGameResultRepository(
        private val createBehavior: (fixtureId: Int, homeScore: Int, awayScore: Int) -> GameResult?
    ) : GameResultRepository {
        override suspend fun getGameByFixtureId(fixtureId: Int): GameResult? = null
        override suspend fun getGamesByFixtureIds(fixtureIds: List<Int>): List<GameResult> = emptyList()
        override suspend fun getAllGames(): List<GameResult> = emptyList()
        override suspend fun createGame(fixtureId: Int, homeScore: Int, awayScore: Int): GameResult? = createBehavior(fixtureId, homeScore, awayScore)
    }

    @Test
    fun execute_returnsComposedGameInfo_onSuccess() = runTest {
        val league = League.L1
        val home = Team(1, "Home", league)
        val away = Team(2, "Away", league)
        val fixture = GameFixture(10, LocalDate.of(2025, 8, 1), 1, home.id, away.id)
        val result = GameResult(10, 3, 2)

        val useCase = ExecuteGameUseCase(
            FakeGameResultRepository { f, h, a -> if (f == 10) GameResult(f, h, a) else null },
            FakeGameFixtureRepository(listOf(fixture)),
            FakeTeamRepository(listOf(home, away))
        )

        val info = useCase.execute(10, result.homeScore, result.awayScore)

        assertEquals(fixture, info.fixture)
        assertEquals(home, info.homeTeam)
        assertEquals(away, info.awayTeam)
        assertEquals(result.homeScore, info.result.homeScore)
        assertEquals(result.awayScore, info.result.awayScore)
    }

    @Test
    fun execute_throws_onDuplicateFixture_orMissingData() = runTest {
        val league = League.L1
        val home = Team(1, "Home", league)
        val away = Team(2, "Away", league)
        val fixture = GameFixture(10, LocalDate.of(2025, 8, 1), 1, home.id, away.id)

        // duplicate (create returns null)
        assertFailsWith<IllegalArgumentException> {
            ExecuteGameUseCase(
                FakeGameResultRepository { _, _, _ -> null },
                FakeGameFixtureRepository(listOf(fixture)),
                FakeTeamRepository(listOf(home, away))
            ).execute(10, 1, 1)
        }

        // missing fixture
        assertFailsWith<IllegalArgumentException> {
            ExecuteGameUseCase(
                FakeGameResultRepository { _, _, _ -> GameResult(10, 1, 1) },
                FakeGameFixtureRepository(emptyList()),
                FakeTeamRepository(listOf(home, away))
            ).execute(10, 1, 1)
        }

        // missing home team
        assertFailsWith<IllegalArgumentException> {
            ExecuteGameUseCase(
                FakeGameResultRepository { _, _, _ -> GameResult(10, 1, 1) },
                FakeGameFixtureRepository(listOf(fixture)),
                FakeTeamRepository(listOf(away))
            ).execute(10, 1, 1)
        }

        // missing away team
        assertFailsWith<IllegalArgumentException> {
            ExecuteGameUseCase(
                FakeGameResultRepository { _, _, _ -> GameResult(10, 1, 1) },
                FakeGameFixtureRepository(listOf(fixture)),
                FakeTeamRepository(listOf(home))
            ).execute(10, 1, 1)
        }
    }
}
