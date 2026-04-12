package com.example.penasim.usecase

import com.example.penasim.features.schedule.domain.GameFixture
import com.example.penasim.features.schedule.domain.GameSchedule
import com.example.penasim.features.game.domain.GameResult
import com.example.penasim.features.game.usecase.GameInfoAssembler
import com.example.penasim.features.game.usecase.GameInfoUseCase
import com.example.penasim.features.team.domain.League
import com.example.penasim.features.team.domain.Team
import com.example.penasim.features.game.domain.repository.GameResultRepository
import com.example.penasim.features.schedule.usecase.GameScheduleResolver
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import kotlin.test.assertFailsWith

class GameInfoUseCaseTest {

    @Test
    fun getById_returnsComposedGameInfo_whenAllDataExists() = runTest {
        val resultRepo: GameResultRepository = mock()
        val resolver: GameScheduleResolver = mock()
        val assembler = GameInfoAssembler()

        val league = League.L1
        val home = Team(1, "Home", league)
        val away = Team(2, "Away", league)
        val fixture = GameFixture(10, LocalDate.of(2025, 8, 1), 1, home.id, away.id)
        val schedule = GameSchedule(fixture, home, away)
        val result = GameResult(10, 5, 2)

        whenever(resolver.getByFixtureId(10)).thenReturn(schedule)
        whenever(resultRepo.getGameByFixtureId(10)).thenReturn(result)

        val useCase = GameInfoUseCase(resultRepo, resolver, assembler)
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
        val schedule = GameSchedule(fixture, home, away)

        val assembler = GameInfoAssembler()

        // Missing schedule
        assertFailsWith<IllegalArgumentException> {
            val resultRepo: GameResultRepository = mock()
            val resolver: GameScheduleResolver = mock()
            whenever(resolver.getByFixtureId(10)).thenReturn(null)
            GameInfoUseCase(resultRepo, resolver, assembler).getById(10)
        }

        // Missing result
        assertFailsWith<IllegalArgumentException> {
            val resultRepo: GameResultRepository = mock()
            val resolver: GameScheduleResolver = mock()
            whenever(resolver.getByFixtureId(10)).thenReturn(schedule)
            whenever(resultRepo.getGameByFixtureId(10)).thenReturn(null)
            GameInfoUseCase(resultRepo, resolver, assembler).getById(10)
        }
    }

    @Test
    fun getByTeam_returnsGameInfos_forTeam() = runTest {
        val resultRepo: GameResultRepository = mock()
        val resolver: GameScheduleResolver = mock()
        val assembler = GameInfoAssembler()

        val league = League.L1
        val team = Team(1, "A", league)
        val opponent = Team(2, "B", league)
        val schedules = listOf(
            GameSchedule(GameFixture(10, LocalDate.now(), 1, 1, 2), team, opponent),
            GameSchedule(GameFixture(11, LocalDate.now(), 2, 2, 1), opponent, team)
        )
        val results = listOf(GameResult(10, 1, 0), GameResult(11, 0, 2))

        whenever(resolver.getByTeam(team)).thenReturn(schedules)
        whenever(resultRepo.getGamesByFixtureIds(listOf(10, 11))).thenReturn(results)

        val useCase = GameInfoUseCase(resultRepo, resolver, assembler)
        val infos = useCase.getByTeam(team)
        assertEquals(2, infos.size)
        assertEquals(10, infos[0].fixture.id)
        assertEquals(11, infos[1].fixture.id)
    }

    @Test
    fun getByTeam_throws_whenResultHasNoMatchingSchedule() = runTest {
        val resultRepo: GameResultRepository = mock()
        val resolver: GameScheduleResolver = mock()
        val assembler = GameInfoAssembler()

        val team = Team(1, "A", League.L1)
        val opponent = Team(2, "B", League.L1)
        val schedules = listOf(GameSchedule(GameFixture(10, LocalDate.now(), 1, 1, 2), team, opponent))
        val results = listOf(GameResult(99, 1, 1))

        whenever(resolver.getByTeam(team)).thenReturn(schedules)
        whenever(resultRepo.getGamesByFixtureIds(listOf(10))).thenReturn(results)

        val useCase = GameInfoUseCase(resultRepo, resolver, assembler)
        assertFailsWith<IllegalArgumentException> { useCase.getByTeam(team) }
    }

    @Test
    fun getByDate_returnsGameInfos_forDate() = runTest {
        val resultRepo: GameResultRepository = mock()
        val resolver: GameScheduleResolver = mock()
        val assembler = GameInfoAssembler()

        val league = League.L1
        val teams = listOf(Team(1, "A", league), Team(2, "B", league))
        val date = LocalDate.of(2025, 7, 1)
        val schedules = listOf(GameSchedule(GameFixture(10, date, 1, 1, 2), teams[0], teams[1]))
        val results = listOf(
            GameResult(10, 2, 0)
        )

        whenever(resolver.getByDate(date)).thenReturn(schedules)
        whenever(resultRepo.getGamesByFixtureIds(listOf(10))).thenReturn(results)

        val useCase = GameInfoUseCase(resultRepo, resolver, assembler)
        val infos = useCase.getByDate(date)
        assertEquals(1, infos.size)
        assertEquals(10, infos[0].fixture.id)
        assertEquals(results[0], infos[0].result)
    }

    @Test
    fun getByDate_throws_whenResultHasNoMatchingSchedule() = runTest {
        val resultRepo: GameResultRepository = mock()
        val resolver: GameScheduleResolver = mock()
        val assembler = GameInfoAssembler()

        val date = LocalDate.now()
        val team = Team(1, "A", League.L1)
        val schedules = listOf(GameSchedule(GameFixture(10, date, 1, 1, 2), team, Team(2, "B", League.L1)))
        val results = listOf(GameResult(99, 1, 1))

        whenever(resolver.getByDate(date)).thenReturn(schedules)
        whenever(resultRepo.getGamesByFixtureIds(listOf(10))).thenReturn(results)

        val useCase = GameInfoUseCase(resultRepo, resolver, assembler)
        assertFailsWith<IllegalArgumentException> { useCase.getByDate(date) }
    }

    @Test
    fun getAll_returnsGameInfos_forAllFixtures() = runTest {
        val resultRepo: GameResultRepository = mock()
        val resolver: GameScheduleResolver = mock()
        val assembler = GameInfoAssembler()

        val league = League.L1
        val teams = listOf(
            Team(1, "A", league), Team(2, "B", league), Team(3, "C", league), Team(4, "D", league)
        )
        val schedules = listOf(
            GameSchedule(GameFixture(10, LocalDate.of(2025, 7, 1), 1, 1, 2), teams[0], teams[1]),
            GameSchedule(GameFixture(11, LocalDate.of(2025, 7, 2), 2, 3, 4), teams[2], teams[3])
        )
        val results = listOf(
            GameResult(10, 3, 1),
            GameResult(11, 0, 0)
        )

        whenever(resolver.getAll()).thenReturn(schedules)
        whenever(resultRepo.getGamesByFixtureIds(listOf(10, 11))).thenReturn(results)

        val useCase = GameInfoUseCase(resultRepo, resolver, assembler)
        val infos = useCase.getAll()
        assertEquals(2, infos.size)
        assertEquals(teams[0], infos[0].homeTeam)
        assertEquals(teams[1], infos[0].awayTeam)
        assertEquals(results[0], infos[0].result)
    }

    @Test
    fun getAll_throws_whenResultHasNoMatchingSchedule() = runTest {
        val resultRepo: GameResultRepository = mock()
        val resolver: GameScheduleResolver = mock()
        val assembler = GameInfoAssembler()

        val league = League.L1
        val teams = listOf(Team(1, "A", league))
        val schedules = listOf(GameSchedule(GameFixture(10, LocalDate.now(), 1, 1, 2), teams[0], Team(2, "B", league)))
        val results = listOf(GameResult(99, 1, 1))

        whenever(resolver.getAll()).thenReturn(schedules)
        whenever(resultRepo.getGamesByFixtureIds(listOf(10))).thenReturn(results)

        val useCase = GameInfoUseCase(resultRepo, resolver, assembler)
        assertFailsWith<IllegalArgumentException> { useCase.getAll() }
    }
}
