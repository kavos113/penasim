package com.example.penasim.ui.game

import com.example.penasim.const.Constants
import com.example.penasim.domain.BattingStat
import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.GameFixture
import com.example.penasim.domain.GameResult
import com.example.penasim.domain.HomeRun
import com.example.penasim.domain.InningScore
import com.example.penasim.domain.League
import com.example.penasim.domain.OrderType
import com.example.penasim.domain.PitcherAppointment
import com.example.penasim.domain.PitcherType
import com.example.penasim.domain.PitchingStat
import com.example.penasim.domain.Player
import com.example.penasim.domain.PlayerPosition
import com.example.penasim.domain.Position
import com.example.penasim.domain.Team
import com.example.penasim.domain.TransactionProvider
import com.example.penasim.domain.repository.BattingStatRepository
import com.example.penasim.domain.repository.FielderAppointmentRepository
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.GameResultRepository
import com.example.penasim.domain.repository.HomeRunRepository
import com.example.penasim.domain.repository.InningScoreRepository
import com.example.penasim.domain.repository.PitcherAppointmentRepository
import com.example.penasim.domain.repository.PitchingStatRepository
import com.example.penasim.domain.repository.PlayerPositionRepository
import com.example.penasim.domain.repository.PlayerRepository
import com.example.penasim.domain.repository.TeamRepository
import com.example.penasim.game.ExecuteGameByOne
import com.example.penasim.testing.MainDispatcherRule
import com.example.penasim.ui.common.GetDisplayFielder
import com.example.penasim.usecase.BattingStatUseCase
import com.example.penasim.usecase.ExecuteGameUseCase
import com.example.penasim.usecase.FielderAppointmentUseCase
import com.example.penasim.usecase.GameInfoUseCase
import com.example.penasim.usecase.GameScheduleUseCase
import com.example.penasim.usecase.HomeRunUseCase
import com.example.penasim.usecase.InningScoreUseCase
import com.example.penasim.usecase.PitcherAppointmentUseCase
import com.example.penasim.usecase.PitchingStatUseCase
import com.example.penasim.usecase.PlayerInfoUseCase
import com.example.penasim.usecase.TeamUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class InGameViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // region Fake Repositories
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
        private val results: MutableList<GameResult> = mutableListOf()
    ) : GameResultRepository {
        override suspend fun getGameByFixtureId(fixtureId: Int): GameResult? = results.find { it.fixtureId == fixtureId }
        override suspend fun getGamesByFixtureIds(fixtureIds: List<Int>): List<GameResult> = results.filter { it.fixtureId in fixtureIds }
        override suspend fun getAllGames(): List<GameResult> = results
        override suspend fun deleteAllGames() { results.clear() }
        override suspend fun createGame(fixtureId: Int, homeScore: Int, awayScore: Int): GameResult? {
            if (results.any { it.fixtureId == fixtureId }) return null
            val r = GameResult(fixtureId, homeScore, awayScore)
            results.add(r)
            return r
        }
    }

    private class FakePlayerRepository(private val players: List<Player>) : PlayerRepository {
        override suspend fun getPlayerCount(teamId: Int): Int = players.count { it.teamId == teamId }
        override suspend fun getPlayers(teamId: Int): List<Player> = players.filter { it.teamId == teamId }
        override suspend fun getPlayer(id: Int): Player? = players.find { it.id == id }
        override suspend fun getAllPlayers(): List<Player> = players
    }

    private class FakePlayerPositionRepository(private val positions: List<PlayerPosition>) : PlayerPositionRepository {
        override suspend fun getPlayerPositions(playerId: Int): List<PlayerPosition> = positions.filter { it.playerId == playerId }
        override suspend fun getAllPlayerPositions(): List<PlayerPosition> = positions
        override suspend fun getAllPlayerPositionsByPosition(position: Position): List<PlayerPosition> = positions.filter { it.position == position }
    }

    private class FakeInningScoreRepository(private val data: MutableList<InningScore> = mutableListOf()) : InningScoreRepository {
        override suspend fun getByFixtureId(fixtureId: Int): List<InningScore> = data.filter { it.fixtureId == fixtureId }
        override suspend fun getByFixtureIds(fixtureIds: List<Int>): List<InningScore> = data.filter { it.fixtureId in fixtureIds }
        override suspend fun getByTeamId(teamId: Int): List<InningScore> = data.filter { it.teamId == teamId }
        override suspend fun getByTeamIds(teamIds: List<Int>): List<InningScore> = data.filter { it.teamId in teamIds }
        override suspend fun insertAll(items: List<InningScore>) { data.addAll(items) }
        override suspend fun deleteByFixtureId(fixtureId: Int) { data.removeAll { it.fixtureId == fixtureId } }
    }

    private class FakePitchingStatRepository(private val data: MutableList<PitchingStat> = mutableListOf()) : PitchingStatRepository {
        override suspend fun getByFixtureId(fixtureId: Int): List<PitchingStat> = data.filter { it.gameFixtureId == fixtureId }
        override suspend fun getByFixtureIds(fixtureIds: List<Int>): List<PitchingStat> = data.filter { it.gameFixtureId in fixtureIds }
        override suspend fun getByPlayerId(playerId: Int): List<PitchingStat> = data.filter { it.playerId == playerId }
        override suspend fun getByPlayerIds(playerIds: List<Int>): List<PitchingStat> = data.filter { it.playerId in playerIds }
        override suspend fun insertAll(items: List<PitchingStat>) { data.addAll(items) }
        override suspend fun deleteByFixtureId(fixtureId: Int) { data.removeAll { it.gameFixtureId == fixtureId } }
    }

    private class FakeBattingStatRepository(private val data: MutableList<BattingStat> = mutableListOf()) : BattingStatRepository {
        override suspend fun getByFixtureId(fixtureId: Int): List<BattingStat> = data.filter { it.gameFixtureId == fixtureId }
        override suspend fun getByFixtureIds(fixtureIds: List<Int>): List<BattingStat> = data.filter { it.gameFixtureId in fixtureIds }
        override suspend fun getByPlayerId(playerId: Int): List<BattingStat> = data.filter { it.playerId == playerId }
        override suspend fun getByPlayerIds(playerIds: List<Int>): List<BattingStat> = data.filter { it.playerId in playerIds }
        override suspend fun insertAll(items: List<BattingStat>) { data.addAll(items) }
        override suspend fun deleteByFixtureId(fixtureId: Int) { data.removeAll { it.gameFixtureId == fixtureId } }
    }

    private class FakeHomeRunRepository(private val data: MutableList<HomeRun> = mutableListOf()) : HomeRunRepository {
        override suspend fun getHomeRunsByFixtureId(fixtureId: Int): List<HomeRun> = data.filter { it.fixtureId == fixtureId }
        override suspend fun getHomeRunsByPlayerId(playerId: Int): List<HomeRun> = data.filter { it.playerId == playerId }
        override suspend fun insertHomeRuns(homeRuns: List<HomeRun>) { data.addAll(homeRuns) }
    }

    private class FakeFielderAppointmentRepository(private val data: List<FielderAppointment>) : FielderAppointmentRepository {
        override suspend fun getFielderAppointmentByPlayerId(playerId: Int) = data.find { it.playerId == playerId }
        override suspend fun getFielderAppointmentsByTeamId(teamId: Int) = data.filter { it.teamId == teamId }
        override suspend fun insertFielderAppointment(fielderAppointment: FielderAppointment) {}
        override suspend fun insertFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
        override suspend fun deleteFielderAppointment(fielderAppointment: FielderAppointment) {}
        override suspend fun deleteFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
        override suspend fun updateFielderAppointment(fielderAppointment: FielderAppointment) {}
        override suspend fun updateFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
    }

    private class FakePitcherAppointmentRepository(private val data: List<PitcherAppointment>) : PitcherAppointmentRepository {
        override suspend fun getPitcherAppointmentsByTeamId(teamId: Int) = data.filter { it.teamId == teamId }
        override suspend fun getPitcherAppointmentByPlayerId(playerId: Int) = data.find { it.playerId == playerId }
        override suspend fun insertPitcherAppointment(pitcherAppointment: PitcherAppointment) {}
        override suspend fun insertPitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {}
        override suspend fun deletePitcherAppointment(pitcherAppointment: PitcherAppointment) {}
        override suspend fun deletePitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {}
        override suspend fun updatePitcherAppointment(pitcherAppointment: PitcherAppointment) {}
        override suspend fun updatePitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {}
    }
    // endregion

    private fun mkPlayer(id: Int, teamId: Int, name: String): Player = Player(
        id = id, firstName = name, lastName = "", teamId = teamId,
        meet = 50, power = 50, speed = 50, throwing = 50, defense = 50, catching = 50,
        ballSpeed = 140, control = 50, stamina = 50, starter = 50, reliever = 50
    )

    /**
     * Creates a minimal valid team with 9 fielders + 1 starting pitcher + 1 reliever.
     * Each position is filled in batting order 1-9, with position 9 mapped to PITCHER.
     */
    private fun createTeamData(teamId: Int, teamName: String, playerIdStart: Int): TeamSetup {
        val team = Team(teamId, teamName, League.L1)

        // 9 fielders in batting order 1-9 + pitcher slot, + 1 reliever
        val startingPositions = listOf(
            Position.CATCHER, Position.FIRST_BASEMAN, Position.SECOND_BASEMAN,
            Position.THIRD_BASEMAN, Position.SHORTSTOP, Position.LEFT_FIELDER,
            Position.CENTER_FIELDER, Position.RIGHT_FIELDER, Position.PITCHER
        )

        val players = (0..10).map { i ->
            mkPlayer(playerIdStart + i, teamId, "P${playerIdStart + i}")
        }

        val fielderAppointments = startingPositions.mapIndexed { idx, pos ->
            FielderAppointment(teamId, players[idx].id, pos, idx + 1, OrderType.NORMAL)
        }

        val pitcherAppointments = listOf(
            PitcherAppointment(teamId, players[9].id, PitcherType.STARTER, 1),
            PitcherAppointment(teamId, players[10].id, PitcherType.RELIEVER, 2),
        )

        val positions = players.map {
            PlayerPosition(it.id, Position.OUTFIELDER, 10)
        }

        return TeamSetup(team, players, fielderAppointments, pitcherAppointments, positions)
    }

    private data class TeamSetup(
        val team: Team,
        val players: List<Player>,
        val fielderAppointments: List<FielderAppointment>,
        val pitcherAppointments: List<PitcherAppointment>,
        val positions: List<PlayerPosition>,
    )

    private fun buildViewModel(
        teams: List<TeamSetup>,
        fixtures: List<GameFixture>,
        results: List<GameResult> = emptyList(),
    ): InGameViewModel {
        val allTeams = teams.map { it.team }
        val allPlayers = teams.flatMap { it.players }
        val allFielders = teams.flatMap { it.fielderAppointments }
        val allPitchers = teams.flatMap { it.pitcherAppointments }
        val allPositions = teams.flatMap { it.positions }

        val teamRepo = FakeTeamRepository(allTeams)
        val fixtureRepo = FakeGameFixtureRepository(fixtures)
        val resultRepo = FakeGameResultRepository(results.toMutableList())
        val playerRepo = FakePlayerRepository(allPlayers)
        val posRepo = FakePlayerPositionRepository(allPositions)
        val battingRepo = FakeBattingStatRepository()
        val pitchingRepo = FakePitchingStatRepository()
        val inningRepo = FakeInningScoreRepository()
        val homeRunRepo = FakeHomeRunRepository()
        val fielderRepo = FakeFielderAppointmentRepository(allFielders)
        val pitcherRepo = FakePitcherAppointmentRepository(allPitchers)

        val scheduleUseCase = GameScheduleUseCase(fixtureRepo, teamRepo)
        val playerInfoUseCase = PlayerInfoUseCase(playerRepo, posRepo, teamRepo, battingRepo, pitchingRepo)
        val fielderUseCase = FielderAppointmentUseCase(fielderRepo)
        val pitcherUseCase = PitcherAppointmentUseCase(pitcherRepo)
        val getDisplayFielder = GetDisplayFielder(playerInfoUseCase, fielderUseCase, pitcherUseCase)

        val teamUseCase = TeamUseCase(teamRepo, playerRepo, fielderRepo, pitcherRepo, posRepo, battingRepo, pitchingRepo)
        val executeGameUseCase = ExecuteGameUseCase(resultRepo, fixtureRepo, teamRepo)
        val battingStatUseCase = BattingStatUseCase(battingRepo)
        val pitchingStatUseCase = PitchingStatUseCase(pitchingRepo)
        val inningUseCase = InningScoreUseCase(inningRepo)
        val homeRunUseCase = HomeRunUseCase(homeRunRepo)
        val gameInfoUseCase = GameInfoUseCase(fixtureRepo, resultRepo, teamRepo)

        val transactionProvider = object : TransactionProvider {
            override suspend fun <T> runInTransaction(block: suspend () -> T): T = block()
        }

        val executeGameByOne = ExecuteGameByOne(
            executeGameUseCase, teamUseCase, scheduleUseCase, gameInfoUseCase,
            battingStatUseCase, pitchingStatUseCase, inningUseCase, homeRunUseCase,
            transactionProvider
        )

        return InGameViewModel(executeGameByOne, scheduleUseCase, getDisplayFielder)
    }

    @Test
    fun defaultState_hasConstantsStartDate() {
        val homeSetup = createTeamData(Constants.TEAM_ID, "Home", 100)
        val awaySetup = createTeamData(1, "Away", 200)

        // No fixture for Constants.START, so we can construct but not call setDate
        val vm = buildViewModel(
            teams = listOf(homeSetup, awaySetup),
            fixtures = emptyList(),
        )

        assertEquals(Constants.START, vm.uiState.value.date)
    }

    @Test
    fun setDate_updatesDateAndInitializesGame() = runTest {
        val homeSetup = createTeamData(Constants.TEAM_ID, "Home", 100)
        val awaySetup = createTeamData(1, "Away", 200)
        val fixture = GameFixture(1, Constants.START, 0, homeSetup.team.id, awaySetup.team.id)

        val vm = buildViewModel(
            teams = listOf(homeSetup, awaySetup),
            fixtures = listOf(fixture),
        )

        vm.setDate(Constants.START)

        val state = vm.uiState.value
        assertEquals(Constants.START, state.date)
        // After initialization, team names should be populated
        assertEquals("Home", state.homeTeam.name)
        assertEquals("Away", state.awayTeam.name)
        // Players should be loaded
        assertTrue(state.homeTeam.players.isNotEmpty())
        assertTrue(state.awayTeam.players.isNotEmpty())
    }

    @Test
    fun setDate_secondCallDoesNotReinitialize() = runTest {
        val homeSetup = createTeamData(Constants.TEAM_ID, "Home", 100)
        val awaySetup = createTeamData(1, "Away", 200)
        val fixture = GameFixture(1, Constants.START, 0, homeSetup.team.id, awaySetup.team.id)

        val vm = buildViewModel(
            teams = listOf(homeSetup, awaySetup),
            fixtures = listOf(fixture),
        )

        vm.setDate(Constants.START)
        val stateAfterFirst = vm.uiState.value

        // Second call changes date but does NOT reinitialize
        val newDate = LocalDate.of(2025, 5, 1)
        vm.setDate(newDate)

        val stateAfterSecond = vm.uiState.value
        assertEquals(newDate, stateAfterSecond.date)
        // Team names should remain from first initialization
        assertEquals(stateAfterFirst.homeTeam.name, stateAfterSecond.homeTeam.name)
        assertEquals(stateAfterFirst.awayTeam.name, stateAfterSecond.awayTeam.name)
    }

    @Test
    fun next_advancesGameAndReturnsState() = runTest {
        val homeSetup = createTeamData(Constants.TEAM_ID, "Home", 100)
        val awaySetup = createTeamData(1, "Away", 200)
        val fixture = GameFixture(1, Constants.START, 0, homeSetup.team.id, awaySetup.team.id)

        val vm = buildViewModel(
            teams = listOf(homeSetup, awaySetup),
            fixtures = listOf(fixture),
        )

        vm.setDate(Constants.START)

        // Execute one play-by-play step
        val finished = vm.next()
        // Game should not be finished in just one step
        assertFalse(finished)
    }

    @Test
    fun skip_completesEntireGame() = runTest {
        val homeSetup = createTeamData(Constants.TEAM_ID, "Home", 100)
        val awaySetup = createTeamData(1, "Away", 200)
        val fixture = GameFixture(1, Constants.START, 0, homeSetup.team.id, awaySetup.team.id)

        val vm = buildViewModel(
            teams = listOf(homeSetup, awaySetup),
            fixtures = listOf(fixture),
        )

        vm.setDate(Constants.START)
        vm.skip()

        val state = vm.uiState.value
        // After skipping the entire game, inning scores should exist for both teams
        assertTrue(state.homeTeam.inningScores.isNotEmpty())
        assertTrue(state.awayTeam.inningScores.isNotEmpty())
    }

    @Test
    fun next_repeatedUntilFinished_completesGame() = runTest {
        val homeSetup = createTeamData(Constants.TEAM_ID, "Home", 100)
        val awaySetup = createTeamData(1, "Away", 200)
        val fixture = GameFixture(1, Constants.START, 0, homeSetup.team.id, awaySetup.team.id)

        val vm = buildViewModel(
            teams = listOf(homeSetup, awaySetup),
            fixtures = listOf(fixture),
        )

        vm.setDate(Constants.START)

        // Run through the entire game step by step
        var steps = 0
        while (!vm.next()) {
            steps++
            // Safety: a baseball game should finish within reasonable steps
            if (steps > 10000) break
        }

        assertTrue("Game should finish within 10000 steps", steps <= 10000)
        val state = vm.uiState.value
        assertTrue(state.homeTeam.inningScores.isNotEmpty())
        assertTrue(state.awayTeam.inningScores.isNotEmpty())
    }

    @Test
    fun setDate_awayTeamMatchesTeamId_initializesCorrectly() = runTest {
        val homeSetup = createTeamData(1, "Home", 100)
        val awaySetup = createTeamData(Constants.TEAM_ID, "Away", 200)
        val fixture = GameFixture(1, Constants.START, 0, homeSetup.team.id, awaySetup.team.id)

        val vm = buildViewModel(
            teams = listOf(homeSetup, awaySetup),
            fixtures = listOf(fixture),
        )

        vm.setDate(Constants.START)

        val state = vm.uiState.value
        assertEquals("Home", state.homeTeam.name)
        assertEquals("Away", state.awayTeam.name)
    }

    @Test
    fun outCount_initiallyZero() {
        val homeSetup = createTeamData(Constants.TEAM_ID, "Home", 100)
        val awaySetup = createTeamData(1, "Away", 200)

        val vm = buildViewModel(
            teams = listOf(homeSetup, awaySetup),
            fixtures = emptyList(),
        )

        assertEquals(0, vm.uiState.value.outCount)
    }

    @Test
    fun bases_initiallyNull() {
        val homeSetup = createTeamData(Constants.TEAM_ID, "Home", 100)
        val awaySetup = createTeamData(1, "Away", 200)

        val vm = buildViewModel(
            teams = listOf(homeSetup, awaySetup),
            fixtures = emptyList(),
        )

        assertEquals(null, vm.uiState.value.firstBase)
        assertEquals(null, vm.uiState.value.secondBase)
        assertEquals(null, vm.uiState.value.thirdBase)
    }
}
