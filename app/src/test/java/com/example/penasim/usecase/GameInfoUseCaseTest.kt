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
import java.time.LocalDate
import kotlin.test.assertFailsWith

class GameInfoUseCaseTest {
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

    private class FakeGameResultRepository(private val results: List<GameResult>) : GameResultRepository {
        override suspend fun getGameByFixtureId(fixtureId: Int): GameResult? = results.find { it.fixtureId == fixtureId }
        override suspend fun getGamesByFixtureIds(fixtureIds: List<Int>): List<GameResult> = results.filter { it.fixtureId in fixtureIds }
        override suspend fun getAllGames(): List<GameResult> = results
        override suspend fun deleteAllGames() {}
        override suspend fun createGame(fixtureId: Int, homeScore: Int, awayScore: Int): GameResult? = null
    }

    @Test
    fun getById_returnsComposedGameInfo_whenAllDataExists() = runTest {
        val league = League.L1
        val home = Team(1, "Home", league)
        val away = Team(2, "Away", league)
        val fixture = GameFixture(10, LocalDate.of(2025, 8, 1), 1, home.id, away.id)
        val result = GameResult(10, 5, 2)

        val useCase = GameInfoUseCase(
            FakeGameFixtureRepository(listOf(fixture)),
            FakeGameResultRepository(listOf(result)),
            FakeTeamRepository(listOf(home, away))
        )

        val info = useCase.getById(10)

        assertEquals(fixture, info.fixture)
        assertEquals(home, info.homeTeam)
        assertEquals(away, info.awayTeam)
        assertEquals(result, info.result)
    }

    @Test
    fun getById_throws_whenTeamsMissing_orResultMissing() = runTest {
        val league = League.L1
        val home = Team(1, "Home", league)
        val away = Team(2, "Away", league)
        val fixture = GameFixture(10, LocalDate.of(2025, 8, 1), 1, home.id, away.id)
        val result = GameResult(10, 5, 2)

        // Missing home team
        assertFailsWith<IllegalArgumentException> {
            GameInfoUseCase(
                FakeGameFixtureRepository(listOf(fixture)),
                FakeGameResultRepository(listOf(result)),
                FakeTeamRepository(listOf(away))
            ).getById(10)
        }

        // Missing away team
        assertFailsWith<IllegalArgumentException> {
            GameInfoUseCase(
                FakeGameFixtureRepository(listOf(fixture)),
                FakeGameResultRepository(listOf(result)),
                FakeTeamRepository(listOf(home))
            ).getById(10)
        }

        // Missing result
        assertFailsWith<IllegalArgumentException> {
            GameInfoUseCase(
                FakeGameFixtureRepository(listOf(fixture)),
                FakeGameResultRepository(emptyList()),
                FakeTeamRepository(listOf(home, away))
            ).getById(10)
        }
    }

    @Test
    fun getByTeam_returnsGameInfos_forTeam() = runTest {
        val league = League.L1
        val team = Team(1, "A", league)
        val opponent = Team(2, "B", league)
        val fixtures = listOf(
            GameFixture(10, LocalDate.now(), 1, 1, 2),
            GameFixture(11, LocalDate.now(), 2, 2, 1)
        )
        val results = listOf(GameResult(10, 1, 0), GameResult(11, 0, 2))

        val useCase = GameInfoUseCase(
            FakeGameFixtureRepository(fixtures),
            FakeGameResultRepository(results),
            FakeTeamRepository(listOf(team, opponent))
        )

        val infos = useCase.getByTeam(team)
        assertEquals(2, infos.size)
        assertEquals(10, infos[0].fixture.id)
        assertEquals(11, infos[1].fixture.id)
    }

    @Test
    fun getByTeam_throws_whenTeamEntityMissing() = runTest {
        val team = Team(1, "A", League.L1)
        val fixtures = listOf(GameFixture(10, LocalDate.now(), 1, 1, 2))
        val results = listOf(GameResult(10, 1, 1))
        val useCase = GameInfoUseCase(
            FakeGameFixtureRepository(fixtures),
            FakeGameResultRepository(results),
            FakeTeamRepository(emptyList())
        )
        assertFailsWith<IllegalArgumentException> { useCase.getByTeam(team) }
    }

    @Test
    fun getByDate_returnsGameInfos_forDate() = runTest {
        val league = League.L1
        val teams = listOf(Team(1, "A", league), Team(2, "B", league))
        val date = LocalDate.of(2025, 7, 1)
        val fixtures = listOf(
            GameFixture(10, date, 1, 1, 2),
            GameFixture(11, date.plusDays(1), 2, 1, 2)
        )
        val results = listOf(
            GameResult(10, 2, 0),
            GameResult(11, 1, 1)
        )
        val useCase = GameInfoUseCase(
            FakeGameFixtureRepository(fixtures),
            FakeGameResultRepository(results),
            FakeTeamRepository(teams)
        )

        val infos = useCase.getByDate(date)
        assertEquals(1, infos.size)
        assertEquals(10, infos[0].fixture.id)
        assertEquals(results[0], infos[0].result)
    }

    @Test
    fun getByDate_throws_whenTeamMissing() = runTest {
        val date = LocalDate.now()
        val fixtures = listOf(GameFixture(10, date, 1, 1, 2))
        val results = listOf(GameResult(10, 1, 1))

        // missing team
        assertFailsWith<IllegalArgumentException> {
            GameInfoUseCase(
                FakeGameFixtureRepository(fixtures),
                FakeGameResultRepository(results),
                FakeTeamRepository(emptyList())
            ).getByDate(date)
        }
    }

    @Test
    fun getAll_returnsGameInfos_forAllFixtures() = runTest {
        val league = League.L1
        val teams = listOf(
            Team(1, "A", league), Team(2, "B", league), Team(3, "C", league), Team(4, "D", league)
        )
        val fixtures = listOf(
            GameFixture(10, LocalDate.of(2025, 7, 1), 1, 1, 2),
            GameFixture(11, LocalDate.of(2025, 7, 2), 2, 3, 4)
        )
        val results = listOf(
            GameResult(10, 3, 1),
            GameResult(11, 0, 0)
        )
        val useCase = GameInfoUseCase(
            FakeGameFixtureRepository(fixtures),
            FakeGameResultRepository(results),
            FakeTeamRepository(teams)
        )

        val infos = useCase.getAll()
        assertEquals(2, infos.size)
        assertEquals(teams[0], infos[0].homeTeam)
        assertEquals(teams[1], infos[0].awayTeam)
        assertEquals(results[0], infos[0].result)
    }

    @Test
    fun getAll_throws_whenTeamMissing() = runTest {
        val league = League.L1
        val teams = listOf(Team(1, "A", league)) // team 2 missing
        val fixtures = listOf(GameFixture(10, LocalDate.now(), 1, 1, 2))
        val results = listOf(GameResult(10, 1, 1))

        // missing team
        assertFailsWith<IllegalArgumentException> {
            GameInfoUseCase(
                FakeGameFixtureRepository(fixtures),
                FakeGameResultRepository(results),
                FakeTeamRepository(teams)
            ).getAll()
        }
    }
}