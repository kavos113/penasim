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

class GameInfoUseCaseTest {

    @Test
    fun getById_returnsComposedGameInfo_whenAllDataExists() = runTest {
        val fixtureRepo: GameFixtureRepository = mock()
        val resultRepo: GameResultRepository = mock()
        val teamRepo: TeamRepository = mock()

        val league = League.L1
        val home = Team(1, "Home", league)
        val away = Team(2, "Away", league)
        val fixture = GameFixture(10, LocalDate.of(2025, 8, 1), 1, home.id, away.id)
        val result = GameResult(10, 5, 2)

        whenever(fixtureRepo.getGameFixture(10)).thenReturn(fixture)
        whenever(teamRepo.getTeam(1)).thenReturn(home)
        whenever(teamRepo.getTeam(2)).thenReturn(away)
        whenever(resultRepo.getGameByFixtureId(10)).thenReturn(result)

        val useCase = GameInfoUseCase(fixtureRepo, resultRepo, teamRepo)
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
            val fixtureRepo: GameFixtureRepository = mock()
            val resultRepo: GameResultRepository = mock()
            val teamRepo: TeamRepository = mock()
            whenever(fixtureRepo.getGameFixture(10)).thenReturn(fixture)
            whenever(teamRepo.getTeam(1)).thenReturn(null)
            whenever(teamRepo.getTeam(2)).thenReturn(away)
            whenever(resultRepo.getGameByFixtureId(10)).thenReturn(result)
            GameInfoUseCase(fixtureRepo, resultRepo, teamRepo).getById(10)
        }

        // Missing away team
        assertFailsWith<IllegalArgumentException> {
            val fixtureRepo: GameFixtureRepository = mock()
            val resultRepo: GameResultRepository = mock()
            val teamRepo: TeamRepository = mock()
            whenever(fixtureRepo.getGameFixture(10)).thenReturn(fixture)
            whenever(teamRepo.getTeam(1)).thenReturn(home)
            whenever(teamRepo.getTeam(2)).thenReturn(null)
            whenever(resultRepo.getGameByFixtureId(10)).thenReturn(result)
            GameInfoUseCase(fixtureRepo, resultRepo, teamRepo).getById(10)
        }

        // Missing result
        assertFailsWith<IllegalArgumentException> {
            val fixtureRepo: GameFixtureRepository = mock()
            val resultRepo: GameResultRepository = mock()
            val teamRepo: TeamRepository = mock()
            whenever(fixtureRepo.getGameFixture(10)).thenReturn(fixture)
            whenever(teamRepo.getTeam(1)).thenReturn(home)
            whenever(teamRepo.getTeam(2)).thenReturn(away)
            whenever(resultRepo.getGameByFixtureId(10)).thenReturn(null)
            GameInfoUseCase(fixtureRepo, resultRepo, teamRepo).getById(10)
        }
    }

    @Test
    fun getByTeam_returnsGameInfos_forTeam() = runTest {
        val fixtureRepo: GameFixtureRepository = mock()
        val resultRepo: GameResultRepository = mock()
        val teamRepo: TeamRepository = mock()

        val league = League.L1
        val team = Team(1, "A", league)
        val opponent = Team(2, "B", league)
        val fixtures = listOf(
            GameFixture(10, LocalDate.now(), 1, 1, 2),
            GameFixture(11, LocalDate.now(), 2, 2, 1)
        )
        val results = listOf(GameResult(10, 1, 0), GameResult(11, 0, 2))

        whenever(fixtureRepo.getGameFixturesByTeam(team)).thenReturn(fixtures)
        whenever(teamRepo.getTeam(1)).thenReturn(team)
        whenever(teamRepo.getTeam(2)).thenReturn(opponent)
        whenever(resultRepo.getGamesByFixtureIds(listOf(10, 11))).thenReturn(results)

        val useCase = GameInfoUseCase(fixtureRepo, resultRepo, teamRepo)
        val infos = useCase.getByTeam(team)
        assertEquals(2, infos.size)
        assertEquals(10, infos[0].fixture.id)
        assertEquals(11, infos[1].fixture.id)
    }

    @Test
    fun getByTeam_throws_whenTeamEntityMissing() = runTest {
        val fixtureRepo: GameFixtureRepository = mock()
        val resultRepo: GameResultRepository = mock()
        val teamRepo: TeamRepository = mock()

        val team = Team(1, "A", League.L1)
        val fixtures = listOf(GameFixture(10, LocalDate.now(), 1, 1, 2))
        val results = listOf(GameResult(10, 1, 1))

        whenever(fixtureRepo.getGameFixturesByTeam(team)).thenReturn(fixtures)
        whenever(teamRepo.getTeam(1)).thenReturn(null)
        whenever(teamRepo.getTeam(2)).thenReturn(null)
        whenever(resultRepo.getGamesByFixtureIds(listOf(10))).thenReturn(results)

        val useCase = GameInfoUseCase(fixtureRepo, resultRepo, teamRepo)
        assertFailsWith<IllegalArgumentException> { useCase.getByTeam(team) }
    }

    @Test
    fun getByDate_returnsGameInfos_forDate() = runTest {
        val fixtureRepo: GameFixtureRepository = mock()
        val resultRepo: GameResultRepository = mock()
        val teamRepo: TeamRepository = mock()

        val league = League.L1
        val teams = listOf(Team(1, "A", league), Team(2, "B", league))
        val date = LocalDate.of(2025, 7, 1)
        val fixtures = listOf(
            GameFixture(10, date, 1, 1, 2)
        )
        val results = listOf(
            GameResult(10, 2, 0)
        )

        whenever(fixtureRepo.getGameFixturesByDate(date)).thenReturn(fixtures)
        whenever(teamRepo.getTeam(1)).thenReturn(teams[0])
        whenever(teamRepo.getTeam(2)).thenReturn(teams[1])
        whenever(resultRepo.getGamesByFixtureIds(listOf(10))).thenReturn(results)

        val useCase = GameInfoUseCase(fixtureRepo, resultRepo, teamRepo)
        val infos = useCase.getByDate(date)
        assertEquals(1, infos.size)
        assertEquals(10, infos[0].fixture.id)
        assertEquals(results[0], infos[0].result)
    }

    @Test
    fun getByDate_throws_whenTeamMissing() = runTest {
        val fixtureRepo: GameFixtureRepository = mock()
        val resultRepo: GameResultRepository = mock()
        val teamRepo: TeamRepository = mock()

        val date = LocalDate.now()
        val fixtures = listOf(GameFixture(10, date, 1, 1, 2))
        val results = listOf(GameResult(10, 1, 1))

        whenever(fixtureRepo.getGameFixturesByDate(date)).thenReturn(fixtures)
        whenever(teamRepo.getTeam(1)).thenReturn(null)
        whenever(teamRepo.getTeam(2)).thenReturn(null)
        whenever(resultRepo.getGamesByFixtureIds(listOf(10))).thenReturn(results)

        val useCase = GameInfoUseCase(fixtureRepo, resultRepo, teamRepo)
        assertFailsWith<IllegalArgumentException> { useCase.getByDate(date) }
    }

    @Test
    fun getAll_returnsGameInfos_forAllFixtures() = runTest {
        val fixtureRepo: GameFixtureRepository = mock()
        val resultRepo: GameResultRepository = mock()
        val teamRepo: TeamRepository = mock()

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

        whenever(fixtureRepo.getAllGameFixtures()).thenReturn(fixtures)
        whenever(teamRepo.getTeam(1)).thenReturn(teams[0])
        whenever(teamRepo.getTeam(2)).thenReturn(teams[1])
        whenever(teamRepo.getTeam(3)).thenReturn(teams[2])
        whenever(teamRepo.getTeam(4)).thenReturn(teams[3])
        whenever(resultRepo.getGamesByFixtureIds(listOf(10, 11))).thenReturn(results)

        val useCase = GameInfoUseCase(fixtureRepo, resultRepo, teamRepo)
        val infos = useCase.getAll()
        assertEquals(2, infos.size)
        assertEquals(teams[0], infos[0].homeTeam)
        assertEquals(teams[1], infos[0].awayTeam)
        assertEquals(results[0], infos[0].result)
    }

    @Test
    fun getAll_throws_whenTeamMissing() = runTest {
        val fixtureRepo: GameFixtureRepository = mock()
        val resultRepo: GameResultRepository = mock()
        val teamRepo: TeamRepository = mock()

        val league = League.L1
        val teams = listOf(Team(1, "A", league)) // team 2 missing
        val fixtures = listOf(GameFixture(10, LocalDate.now(), 1, 1, 2))
        val results = listOf(GameResult(10, 1, 1))

        whenever(fixtureRepo.getAllGameFixtures()).thenReturn(fixtures)
        whenever(teamRepo.getTeam(1)).thenReturn(teams[0])
        whenever(teamRepo.getTeam(2)).thenReturn(null)
        whenever(resultRepo.getGamesByFixtureIds(listOf(10))).thenReturn(results)

        val useCase = GameInfoUseCase(fixtureRepo, resultRepo, teamRepo)
        assertFailsWith<IllegalArgumentException> { useCase.getAll() }
    }
}