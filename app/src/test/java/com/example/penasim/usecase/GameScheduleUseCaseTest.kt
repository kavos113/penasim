package com.example.penasim.usecase

import com.example.penasim.domain.GameFixture
import com.example.penasim.domain.League
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.TeamRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import kotlin.test.assertFailsWith

class GameScheduleUseCaseTest {

    @Test
    fun getByFixtureId_returnsSchedule_whenDataExists() = runTest {
        val fixtureRepo: GameFixtureRepository = mock()
        val teamRepo: TeamRepository = mock()

        val league = League.L1
        val home = Team(1, "H", league)
        val away = Team(2, "A", league)
        val fixture = GameFixture(10, LocalDate.now(), 1, 1, 2)

        whenever(fixtureRepo.getGameFixture(10)).thenReturn(fixture)
        whenever(teamRepo.getTeam(1)).thenReturn(home)
        whenever(teamRepo.getTeam(2)).thenReturn(away)

        val useCase = GameScheduleUseCase(fixtureRepo, teamRepo)
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
            val fixtureRepo: GameFixtureRepository = mock()
            val teamRepo: TeamRepository = mock()
            whenever(fixtureRepo.getGameFixture(10)).thenReturn(null)
            GameScheduleUseCase(fixtureRepo, teamRepo).getByFixtureId(10)
        }
        // Missing home team
        assertFailsWith<IllegalArgumentException> {
            val fixtureRepo: GameFixtureRepository = mock()
            val teamRepo: TeamRepository = mock()
            whenever(fixtureRepo.getGameFixture(10)).thenReturn(fixture)
            whenever(teamRepo.getTeam(2)).thenReturn(away)
            GameScheduleUseCase(fixtureRepo, teamRepo).getByFixtureId(10)
        }
        // Missing away team
        assertFailsWith<IllegalArgumentException> {
            val fixtureRepo: GameFixtureRepository = mock()
            val teamRepo: TeamRepository = mock()
            whenever(fixtureRepo.getGameFixture(10)).thenReturn(fixture)
            whenever(teamRepo.getTeam(1)).thenReturn(home)
            GameScheduleUseCase(fixtureRepo, teamRepo).getByFixtureId(10)
        }
    }

    @Test
    fun getByTeam_returnsSchedules_forTeam() = runTest {
        val fixtureRepo: GameFixtureRepository = mock()
        val teamRepo: TeamRepository = mock()

        val league = League.L1
        val team = Team(1, "A", league)
        val opponent = Team(2, "B", league)
        val fixtures = listOf(
            GameFixture(10, LocalDate.now(), 1, 1, 2),
            GameFixture(11, LocalDate.now(), 2, 2, 1)
        )

        whenever(fixtureRepo.getGameFixturesByTeam(team)).thenReturn(fixtures)
        whenever(teamRepo.getTeam(1)).thenReturn(team)
        whenever(teamRepo.getTeam(2)).thenReturn(opponent)

        val useCase = GameScheduleUseCase(fixtureRepo, teamRepo)
        val schedules = useCase.getByTeam(team)
        assertEquals(2, schedules.size)
        assertEquals(10, schedules[0].fixture.id)
        assertEquals(11, schedules[1].fixture.id)
    }

    @Test
    fun getByTeam_throws_whenTeamMissing() = runTest {
        val fixtureRepo: GameFixtureRepository = mock()
        val teamRepo: TeamRepository = mock()

        val team = Team(1, "A", League.L1)
        val fixtures = listOf(GameFixture(10, LocalDate.now(), 1, 1, 2))

        whenever(fixtureRepo.getGameFixturesByTeam(team)).thenReturn(fixtures)
        // Do not mock getTeam — Mockito returns null by default, so teams list is empty

        val useCase = GameScheduleUseCase(fixtureRepo, teamRepo)
        assertFailsWith<IllegalArgumentException> { useCase.getByTeam(team) }
    }

    @Test
    fun getByDate_returnsSchedules_forDate() = runTest {
        val fixtureRepo: GameFixtureRepository = mock()
        val teamRepo: TeamRepository = mock()

        val league = League.L1
        val teamA = Team(1, "A", league)
        val teamB = Team(2, "B", league)
        val date = LocalDate.of(2025, 7, 1)
        val fixtures = listOf(GameFixture(10, date, 1, 1, 2))

        whenever(fixtureRepo.getGameFixturesByDate(date)).thenReturn(fixtures)
        whenever(teamRepo.getTeam(1)).thenReturn(teamA)
        whenever(teamRepo.getTeam(2)).thenReturn(teamB)

        val useCase = GameScheduleUseCase(fixtureRepo, teamRepo)
        val schedules = useCase.getByDate(date)
        assertEquals(1, schedules.size)
        assertEquals(10, schedules[0].fixture.id)
    }

    @Test
    fun getByDate_throws_whenTeamMissing() = runTest {
        val fixtureRepo: GameFixtureRepository = mock()
        val teamRepo: TeamRepository = mock()

        val date = LocalDate.now()
        val fixtures = listOf(GameFixture(10, date, 1, 1, 2))

        whenever(fixtureRepo.getGameFixturesByDate(date)).thenReturn(fixtures)

        val useCase = GameScheduleUseCase(fixtureRepo, teamRepo)
        assertFailsWith<IllegalArgumentException> { useCase.getByDate(date) }
    }

    @Test
    fun getAll_returnsSchedules_forAllFixtures() = runTest {
        val fixtureRepo: GameFixtureRepository = mock()
        val teamRepo: TeamRepository = mock()

        val league = League.L1
        val teamA = Team(1, "A", league)
        val teamB = Team(2, "B", league)
        val fixtures = listOf(
            GameFixture(10, LocalDate.now(), 1, 1, 2),
            GameFixture(11, LocalDate.now().plusDays(1), 2, 2, 1)
        )

        whenever(fixtureRepo.getAllGameFixtures()).thenReturn(fixtures)
        whenever(teamRepo.getTeam(1)).thenReturn(teamA)
        whenever(teamRepo.getTeam(2)).thenReturn(teamB)

        val useCase = GameScheduleUseCase(fixtureRepo, teamRepo)
        val schedules = useCase.getAll()
        assertEquals(2, schedules.size)
        assertEquals(1, schedules.first().homeTeam.id)
        assertEquals(2, schedules.first().awayTeam.id)
    }

    @Test
    fun getAll_throws_whenTeamMissing() = runTest {
        val fixtureRepo: GameFixtureRepository = mock()
        val teamRepo: TeamRepository = mock()

        val fixtures = listOf(GameFixture(10, LocalDate.now(), 1, 1, 2))

        whenever(fixtureRepo.getAllGameFixtures()).thenReturn(fixtures)

        val useCase = GameScheduleUseCase(fixtureRepo, teamRepo)
        assertFailsWith<IllegalArgumentException> { useCase.getAll() }
    }
}