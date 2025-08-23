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

class ExecuteGamesByDateUseCaseTest {

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
        val created = mutableListOf<GameResult>()
        override suspend fun getGameByFixtureId(fixtureId: Int): GameResult? = created.find { it.fixtureId == fixtureId }
        override suspend fun getGamesByFixtureIds(fixtureIds: List<Int>): List<GameResult> = created.filter { it.fixtureId in fixtureIds }
        override suspend fun getAllGames(): List<GameResult> = created
        override suspend fun createGame(fixtureId: Int, homeScore: Int, awayScore: Int): GameResult? =
            createBehavior(fixtureId, homeScore, awayScore)?.also { created.add(it) }
    }

    @Test
    fun execute_returnsGameInfosForAllFixturesOnDate() = runTest {
        val date = LocalDate.of(2025, 8, 1)
        val league = League.L1
        val home1 = Team(1, "H1", league)
        val away1 = Team(2, "A1", league)
        val home2 = Team(3, "H2", league)
        val away2 = Team(4, "A2", league)
        val fixtures = listOf(
            GameFixture(10, date, 1, home1.id, away1.id),
            GameFixture(11, date, 1, home2.id, away2.id)
        )
        val resultRepo = FakeGameResultRepository { f, h, a -> GameResult(f, h, a) }
        val useCase = ExecuteGamesByDateUseCase(
            resultRepo,
            FakeGameFixtureRepository(fixtures),
            FakeTeamRepository(listOf(home1, away1, home2, away2))
        )

        val infos = useCase.execute(date)

        assertEquals(2, infos.size)
        assertEquals(setOf(10, 11), infos.map { it.fixture.id }.toSet())
        // Scores are random, but result is present for each fixture
        assertEquals(setOf(10, 11), resultRepo.created.map { it.fixtureId }.toSet())
    }

    @Test
    fun execute_throws_whenTeamMissing_orResultMissing() = runTest {
        val date = LocalDate.of(2025, 8, 1)
        val league = League.L1
        val home = Team(1, "H1", league)
        val away = Team(2, "A1", league)
        val fixtures = listOf(GameFixture(10, date, 1, home.id, away.id))

        // team missing
        assertFailsWith<IllegalArgumentException> {
            ExecuteGamesByDateUseCase(
                FakeGameResultRepository { f, h, a -> GameResult(f, h, a) },
                FakeGameFixtureRepository(fixtures),
                FakeTeamRepository(emptyList())
            ).execute(date)
        }

        // result missing for fixture
        assertFailsWith<IllegalArgumentException> {
            ExecuteGamesByDateUseCase(
                FakeGameResultRepository { f, h, a -> if (f == 10) null else GameResult(f, h, a) },
                FakeGameFixtureRepository(fixtures),
                FakeTeamRepository(listOf(home, away))
            ).execute(date)
        }
    }
}
