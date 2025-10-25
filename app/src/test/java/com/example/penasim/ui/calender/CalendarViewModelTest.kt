package com.example.penasim.ui.calender

import com.example.penasim.const.Constants
import com.example.penasim.domain.GameFixture
import com.example.penasim.domain.GameResult
import com.example.penasim.domain.League
import com.example.penasim.domain.Team
import com.example.penasim.domain.TransactionProvider
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.GameResultRepository
import com.example.penasim.domain.repository.TeamRepository
import com.example.penasim.testing.MainDispatcherRule
import com.example.penasim.usecase.GameInfoUseCase
import com.example.penasim.usecase.GameScheduleUseCase
import com.example.penasim.usecase.RankingUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class CalendarViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

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
    fun init_buildsEmptyGames_whenNoSchedulesOrResults() = runTest {
        val teamRepo = FakeTeamRepository(emptyList())
        val fixtureRepo = FakeGameFixtureRepository(emptyList())
        val resultRepo = FakeGameResultRepository(emptyList())

        val gameInfoUseCase = GameInfoUseCase(fixtureRepo, resultRepo, teamRepo)
        val gameScheduleUseCase = GameScheduleUseCase(fixtureRepo, teamRepo)
        val rankingUseCase = RankingUseCase(teamRepo, fixtureRepo, resultRepo)

        val vm = CalendarViewModel(gameScheduleUseCase, gameInfoUseCase, rankingUseCase, dummyExecutor())

        // currentDay should be START when there is no game info
        assertEquals(Constants.START, vm.uiState.value.currentDay)
        // games map should contain START with empty list
        assertTrue(vm.uiState.value.games.containsKey(Constants.START))
        assertTrue(vm.uiState.value.games[Constants.START]?.isEmpty() == true)
        // rankings should be empty when team repo has no teams
        assertTrue(vm.uiState.value.rankings.isEmpty())
    }

    @Test
    fun nextGame_updatesGamesForCurrentDay_withEmptyRecentGames() = runTest {
        val teamRepo = FakeTeamRepository(emptyList())
        val fixtureRepo = FakeGameFixtureRepository(emptyList())
        val resultRepo = FakeGameResultRepository(emptyList())

        val gameInfoUseCase = GameInfoUseCase(fixtureRepo, resultRepo, teamRepo)
        val gameScheduleUseCase = GameScheduleUseCase(fixtureRepo, teamRepo)
        val rankingUseCase = RankingUseCase(teamRepo, fixtureRepo, resultRepo)

        val vm = CalendarViewModel(gameScheduleUseCase, gameInfoUseCase, rankingUseCase, dummyExecutor())

        val day = vm.uiState.value.currentDay
        vm.nextGame()

        assertTrue(vm.uiState.value.games.containsKey(day))
        assertTrue(vm.uiState.value.games[day]?.isEmpty() == true)
    }

    private fun dummyExecutor(): com.example.penasim.game.ExecuteGamesByDate {
        // Provide a real instance whose dependencies will no-op due to empty schedules
        val teamRepo = FakeTeamRepository(emptyList())
        val fixtureRepo = FakeGameFixtureRepository(emptyList())
        val resultRepo = FakeGameResultRepository(emptyList())

        val scheduleUseCase = GameScheduleUseCase(fixtureRepo, teamRepo)

        val battingRepo = object : com.example.penasim.domain.repository.BattingStatRepository {
            override suspend fun getByFixtureId(fixtureId: Int) = emptyList<com.example.penasim.domain.BattingStat>()
            override suspend fun getByFixtureIds(fixtureIds: List<Int>) = emptyList<com.example.penasim.domain.BattingStat>()
            override suspend fun getByPlayerId(playerId: Int) = emptyList<com.example.penasim.domain.BattingStat>()
            override suspend fun getByPlayerIds(playerIds: List<Int>) = emptyList<com.example.penasim.domain.BattingStat>()
            override suspend fun insertAll(items: List<com.example.penasim.domain.BattingStat>) {}
            override suspend fun deleteByFixtureId(fixtureId: Int) {}
        }
        val batting = com.example.penasim.usecase.BattingStatUseCase(battingRepo)

        val pitchingRepo = object : com.example.penasim.domain.repository.PitchingStatRepository {
            override suspend fun getByFixtureId(fixtureId: Int) = emptyList<com.example.penasim.domain.PitchingStat>()
            override suspend fun getByFixtureIds(fixtureIds: List<Int>) = emptyList<com.example.penasim.domain.PitchingStat>()
            override suspend fun getByPlayerId(playerId: Int) = emptyList<com.example.penasim.domain.PitchingStat>()
            override suspend fun getByPlayerIds(playerIds: List<Int>) = emptyList<com.example.penasim.domain.PitchingStat>()
            override suspend fun insertAll(items: List<com.example.penasim.domain.PitchingStat>) {}
            override suspend fun deleteByFixtureId(fixtureId: Int) {}
        }
        val pitching = com.example.penasim.usecase.PitchingStatUseCase(pitchingRepo)

        val inning = com.example.penasim.usecase.InningScoreUseCase(object : com.example.penasim.domain.repository.InningScoreRepository {
            override suspend fun getByFixtureId(fixtureId: Int) = emptyList<com.example.penasim.domain.InningScore>()
            override suspend fun getByFixtureIds(fixtureIds: List<Int>) = emptyList<com.example.penasim.domain.InningScore>()
            override suspend fun getByTeamId(teamId: Int) = emptyList<com.example.penasim.domain.InningScore>()
            override suspend fun getByTeamIds(teamIds: List<Int>) = emptyList<com.example.penasim.domain.InningScore>()
            override suspend fun insertAll(items: List<com.example.penasim.domain.InningScore>) {}
            override suspend fun deleteByFixtureId(fixtureId: Int) {}
        })
        val homeRun = com.example.penasim.usecase.HomeRunUseCase(object : com.example.penasim.domain.repository.HomeRunRepository {
            override suspend fun getHomeRunsByFixtureId(fixtureId: Int) = emptyList<com.example.penasim.domain.HomeRun>()
            override suspend fun getHomeRunsByPlayerId(playerId: Int) = emptyList<com.example.penasim.domain.HomeRun>()
            override suspend fun insertHomeRuns(homeRuns: List<com.example.penasim.domain.HomeRun>) {}
        })

        val teamUseCase = com.example.penasim.usecase.TeamUseCase(
            teamRepo,
            object : com.example.penasim.domain.repository.PlayerRepository {
                override suspend fun getPlayerCount(teamId: Int): Int = 0
                override suspend fun getPlayers(teamId: Int) = emptyList<com.example.penasim.domain.Player>()
                override suspend fun getPlayer(id: Int) = null
                override suspend fun getAllPlayers(): List<com.example.penasim.domain.Player> = emptyList()
            },
            object : com.example.penasim.domain.repository.FielderAppointmentRepository {
                override suspend fun getFielderAppointmentByPlayerId(playerId: Int) = null
                override suspend fun getFielderAppointmentsByTeamId(teamId: Int) = emptyList<com.example.penasim.domain.FielderAppointment>()
                override suspend fun insertFielderAppointment(fielderAppointment: com.example.penasim.domain.FielderAppointment) {}
                override suspend fun insertFielderAppointments(fielderAppointments: List<com.example.penasim.domain.FielderAppointment>) {}
                override suspend fun deleteFielderAppointment(fielderAppointment: com.example.penasim.domain.FielderAppointment) {}
                override suspend fun deleteFielderAppointments(fielderAppointments: List<com.example.penasim.domain.FielderAppointment>) {}
                override suspend fun updateFielderAppointment(fielderAppointment: com.example.penasim.domain.FielderAppointment) {}
                override suspend fun updateFielderAppointments(fielderAppointments: List<com.example.penasim.domain.FielderAppointment>) {}
            },
            object : com.example.penasim.domain.repository.PitcherAppointmentRepository {
                override suspend fun getPitcherAppointmentsByTeamId(teamId: Int) = emptyList<com.example.penasim.domain.PitcherAppointment>()
                override suspend fun getPitcherAppointmentByPlayerId(playerId: Int) = null
                override suspend fun insertPitcherAppointment(pitcherAppointment: com.example.penasim.domain.PitcherAppointment) {}
                override suspend fun insertPitcherAppointments(pitcherAppointments: List<com.example.penasim.domain.PitcherAppointment>) {}
                override suspend fun deletePitcherAppointment(pitcherAppointment: com.example.penasim.domain.PitcherAppointment) {}
                override suspend fun deletePitcherAppointments(pitcherAppointments: List<com.example.penasim.domain.PitcherAppointment>) {}
                override suspend fun updatePitcherAppointment(pitcherAppointment: com.example.penasim.domain.PitcherAppointment) {}
                override suspend fun updatePitcherAppointments(pitcherAppointments: List<com.example.penasim.domain.PitcherAppointment>) {}
            },
            object : com.example.penasim.domain.repository.PlayerPositionRepository {
                override suspend fun getPlayerPositions(playerId: Int) = emptyList<com.example.penasim.domain.PlayerPosition>()
                override suspend fun getAllPlayerPositions(): List<com.example.penasim.domain.PlayerPosition> = emptyList()
                override suspend fun getAllPlayerPositionsByPosition(position: com.example.penasim.domain.Position): List<com.example.penasim.domain.PlayerPosition> = emptyList()
            },
            battingRepo,
            pitchingRepo,
        )

        val executeGame = com.example.penasim.usecase.ExecuteGameUseCase(
            resultRepo,
            fixtureRepo,
            teamRepo
        )

        return com.example.penasim.game.ExecuteGamesByDate(
            executeGame,
            teamUseCase,
            scheduleUseCase,
            batting,
            pitching,
            inning,
            homeRun,
            object : TransactionProvider { override suspend fun <T> runInTransaction(block: suspend () -> T): T = block() }
        )
    }
}
