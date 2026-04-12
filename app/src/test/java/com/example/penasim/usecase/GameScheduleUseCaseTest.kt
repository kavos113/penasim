package com.example.penasim.usecase

import com.example.penasim.features.schedule.domain.GameFixture
import com.example.penasim.features.schedule.domain.GameSchedule
import com.example.penasim.features.team.domain.League
import com.example.penasim.features.team.domain.Team
import com.example.penasim.features.schedule.usecase.GameScheduleResolver
import com.example.penasim.features.schedule.usecase.GameScheduleUseCase
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
        val resolver: GameScheduleResolver = mock()

        val league = League.L1
        val home = Team(1, "H", league)
        val away = Team(2, "A", league)
        val fixture = GameFixture(10, LocalDate.now(), 1, 1, 2)
        val schedule = GameSchedule(fixture, home, away)

        whenever(resolver.getByFixtureId(10)).thenReturn(schedule)

        val useCase = GameScheduleUseCase(resolver)
        val actual = useCase.getByFixtureId(10)
        assertEquals(fixture, actual.fixture)
        assertEquals(home, actual.homeTeam)
        assertEquals(away, actual.awayTeam)
    }

    @Test
    fun getByFixtureId_throws_whenMissing() = runTest {
        val resolver: GameScheduleResolver = mock()
        whenever(resolver.getByFixtureId(10)).thenReturn(null)

        assertFailsWith<IllegalArgumentException> {
            GameScheduleUseCase(resolver).getByFixtureId(10)
        }
    }

    @Test
    fun getByTeam_returnsSchedules_forTeam() = runTest {
        val resolver: GameScheduleResolver = mock()

        val league = League.L1
        val team = Team(1, "A", league)
        val opponent = Team(2, "B", league)
        val schedules = listOf(
            GameSchedule(GameFixture(10, LocalDate.now(), 1, 1, 2), team, opponent),
            GameSchedule(GameFixture(11, LocalDate.now(), 2, 2, 1), opponent, team)
        )

        whenever(resolver.getByTeam(team)).thenReturn(schedules)

        val useCase = GameScheduleUseCase(resolver)
        val actual = useCase.getByTeam(team)
        assertEquals(2, actual.size)
        assertEquals(10, actual[0].fixture.id)
        assertEquals(11, actual[1].fixture.id)
    }

    @Test
    fun getByTeam_returnsEmpty_whenResolverReturnsEmpty() = runTest {
        val resolver: GameScheduleResolver = mock()
        val team = Team(1, "A", League.L1)
        whenever(resolver.getByTeam(team)).thenReturn(emptyList())

        val actual = GameScheduleUseCase(resolver).getByTeam(team)
        assertEquals(emptyList<GameSchedule>(), actual)
    }

    @Test
    fun getByDate_returnsSchedules_forDate() = runTest {
        val resolver: GameScheduleResolver = mock()

        val league = League.L1
        val teamA = Team(1, "A", league)
        val teamB = Team(2, "B", league)
        val date = LocalDate.of(2025, 7, 1)
        val schedules = listOf(GameSchedule(GameFixture(10, date, 1, 1, 2), teamA, teamB))

        whenever(resolver.getByDate(date)).thenReturn(schedules)

        val useCase = GameScheduleUseCase(resolver)
        val actual = useCase.getByDate(date)
        assertEquals(1, actual.size)
        assertEquals(10, actual[0].fixture.id)
    }

    @Test
    fun getByDate_returnsEmpty_whenResolverReturnsEmpty() = runTest {
        val resolver: GameScheduleResolver = mock()
        val date = LocalDate.now()
        whenever(resolver.getByDate(date)).thenReturn(emptyList())

        val actual = GameScheduleUseCase(resolver).getByDate(date)
        assertEquals(emptyList<GameSchedule>(), actual)
    }

    @Test
    fun getAll_returnsSchedules_forAllFixtures() = runTest {
        val resolver: GameScheduleResolver = mock()

        val league = League.L1
        val teamA = Team(1, "A", league)
        val teamB = Team(2, "B", league)
        val schedules = listOf(
            GameSchedule(GameFixture(10, LocalDate.now(), 1, 1, 2), teamA, teamB),
            GameSchedule(GameFixture(11, LocalDate.now().plusDays(1), 2, 2, 1), teamB, teamA)
        )

        whenever(resolver.getAll()).thenReturn(schedules)

        val useCase = GameScheduleUseCase(resolver)
        val actual = useCase.getAll()
        assertEquals(2, actual.size)
        assertEquals(1, actual.first().homeTeam.id)
        assertEquals(2, actual.first().awayTeam.id)
    }

    @Test
    fun getAll_returnsEmpty_whenResolverReturnsEmpty() = runTest {
        val resolver: GameScheduleResolver = mock()
        whenever(resolver.getAll()).thenReturn(emptyList())

        val actual = GameScheduleUseCase(resolver).getAll()
        assertEquals(emptyList<GameSchedule>(), actual)
    }
}
