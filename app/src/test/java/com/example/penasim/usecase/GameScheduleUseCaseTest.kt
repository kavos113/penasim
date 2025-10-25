package com.example.penasim.usecase

import com.example.penasim.domain.GameFixture
import com.example.penasim.domain.League
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.TeamRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertFailsWith

class GameScheduleUseCaseTest {

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

    @Test
    fun getByFixtureId_returnsSchedule_whenDataExists() = runTest {
        val league = League.L1
        val home = Team(1, "H", league)
        val away = Team(2, "A", league)
        val fixture = GameFixture(10, LocalDate.now(), 1, 1, 2)
        val useCase = GameScheduleUseCase(
            FakeGameFixtureRepository(listOf(fixture)),
            FakeTeamRepository(listOf(home, away))
        )
        val schedule = useCase.getByFixtureId(10)
        assertEquals(fixture, schedule.fixture)
        assertEquals(home, schedule.homeTeam)
        assertEquals(away, schedule.awayTeam)
    }

    @Test
    fun getByFixtureId_throws_whenMissing() = runTest {
        val league = League.L1
        val home = Team(1, "H", league)
        val away = Team(2, "A", league)
        val fixture = GameFixture(10, LocalDate.now(), 1, 1, 2)

        // Missing fixture
        assertFailsWith<IllegalArgumentException> {
            GameScheduleUseCase(
                FakeGameFixtureRepository(emptyList()),
                FakeTeamRepository(listOf(home, away))
            ).getByFixtureId(10)
        }
        // Missing home team
        assertFailsWith<IllegalArgumentException> {
            GameScheduleUseCase(
                FakeGameFixtureRepository(listOf(fixture)),
                FakeTeamRepository(listOf(away))
            ).getByFixtureId(10)
        }
        // Missing away team
        assertFailsWith<IllegalArgumentException> {
            GameScheduleUseCase(
                FakeGameFixtureRepository(listOf(fixture)),
                FakeTeamRepository(listOf(home))
            ).getByFixtureId(10)
        }
    }

    @Test
    fun getByTeam_returnsSchedules_forTeam() = runTest {
        val league = League.L1
        val team = Team(1, "A", league)
        val opponent = Team(2, "B", league)
        val fixtures = listOf(
            GameFixture(10, LocalDate.now(), 1, 1, 2),
            GameFixture(11, LocalDate.now(), 2, 2, 1)
        )
        val useCase = GameScheduleUseCase(
            FakeGameFixtureRepository(fixtures),
            FakeTeamRepository(listOf(team, opponent))
        )
        val schedules = useCase.getByTeam(team)
        assertEquals(2, schedules.size)
        assertEquals(10, schedules[0].fixture.id)
        assertEquals(11, schedules[1].fixture.id)
    }

    @Test
    fun getByTeam_throws_whenTeamMissing() = runTest {
        val team = Team(1, "A", League.L1)
        val fixtures = listOf(GameFixture(10, LocalDate.now(), 1, 1, 2))
        val useCase = GameScheduleUseCase(
            FakeGameFixtureRepository(fixtures),
            FakeTeamRepository(emptyList())
        )
        assertFailsWith<IllegalArgumentException> { useCase.getByTeam(team) }
    }

    @Test
    fun getByDate_returnsSchedules_forDate() = runTest {
        val league = League.L1
        val teams = listOf(Team(1, "A", league), Team(2, "B", league))
        val date = LocalDate.of(2025, 7, 1)
        val fixtures = listOf(
            GameFixture(10, date, 1, 1, 2),
            GameFixture(11, date.plusDays(1), 2, 2, 1)
        )
        val useCase = GameScheduleUseCase(
            FakeGameFixtureRepository(fixtures),
            FakeTeamRepository(teams)
        )
        val schedules = useCase.getByDate(date)
        assertEquals(1, schedules.size)
        assertEquals(10, schedules[0].fixture.id)
    }

    @Test
    fun getByDate_throws_whenTeamMissing() = runTest {
        val date = LocalDate.now()
        val fixtures = listOf(GameFixture(10, date, 1, 1, 2))
        val useCase = GameScheduleUseCase(
            FakeGameFixtureRepository(fixtures),
            FakeTeamRepository(emptyList())
        )
        assertFailsWith<IllegalArgumentException> { useCase.getByDate(date) }
    }

    @Test
    fun getAll_returnsSchedules_forAllFixtures() = runTest {
        val league = League.L1
        val teams = listOf(Team(1, "A", league), Team(2, "B", league))
        val fixtures = listOf(
            GameFixture(10, LocalDate.now(), 1, 1, 2),
            GameFixture(11, LocalDate.now().plusDays(1), 2, 2, 1)
        )
        val useCase = GameScheduleUseCase(
            FakeGameFixtureRepository(fixtures),
            FakeTeamRepository(teams)
        )
        val schedules = useCase.getAll()
        assertEquals(2, schedules.size)
        assertEquals(1, schedules.first().homeTeam.id)
        assertEquals(2, schedules.first().awayTeam.id)
    }

    @Test
    fun getAll_throws_whenTeamMissing() = runTest {
        val fixtures = listOf(GameFixture(10, LocalDate.now(), 1, 1, 2))
        val useCase = GameScheduleUseCase(
            FakeGameFixtureRepository(fixtures),
            FakeTeamRepository(emptyList())
        )
        assertFailsWith<IllegalArgumentException> { useCase.getAll() }
    }
}