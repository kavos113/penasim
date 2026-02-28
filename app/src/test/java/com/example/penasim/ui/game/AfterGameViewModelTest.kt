package com.example.penasim.ui.game

import com.example.penasim.const.Constants
import com.example.penasim.domain.BattingStat
import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.GameFixture
import com.example.penasim.domain.GameInfo
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
import com.example.penasim.game.ExecuteGamesByDate
import com.example.penasim.testing.MainDispatcherRule
import com.example.penasim.usecase.BattingStatUseCase
import com.example.penasim.usecase.ExecuteGameUseCase
import com.example.penasim.usecase.GameInfoUseCase
import com.example.penasim.usecase.GameScheduleUseCase
import com.example.penasim.usecase.HomeRunUseCase
import com.example.penasim.usecase.InningScoreUseCase
import com.example.penasim.usecase.PitchingStatUseCase
import com.example.penasim.usecase.PlayerInfoUseCase
import com.example.penasim.usecase.RankingUseCase
import com.example.penasim.usecase.TeamUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class AfterGameViewModelTest {
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

    private class FakeFielderAppointmentRepository(private val data: List<FielderAppointment> = emptyList()) : FielderAppointmentRepository {
        override suspend fun getFielderAppointmentByPlayerId(playerId: Int) = data.find { it.playerId == playerId }
        override suspend fun getFielderAppointmentsByTeamId(teamId: Int) = data.filter { it.teamId == teamId }
        override suspend fun insertFielderAppointment(fielderAppointment: FielderAppointment) {}
        override suspend fun insertFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
        override suspend fun deleteFielderAppointment(fielderAppointment: FielderAppointment) {}
        override suspend fun deleteFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
        override suspend fun updateFielderAppointment(fielderAppointment: FielderAppointment) {}
        override suspend fun updateFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
    }

    private class FakePitcherAppointmentRepository(private val data: List<PitcherAppointment> = emptyList()) : PitcherAppointmentRepository {
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

    private data class TestDeps(
        val teamRepo: FakeTeamRepository = FakeTeamRepository(emptyList()),
        val fixtureRepo: FakeGameFixtureRepository = FakeGameFixtureRepository(emptyList()),
        val resultRepo: FakeGameResultRepository = FakeGameResultRepository(),
        val playerRepo: FakePlayerRepository = FakePlayerRepository(emptyList()),
        val posRepo: FakePlayerPositionRepository = FakePlayerPositionRepository(emptyList()),
        val inningRepo: FakeInningScoreRepository = FakeInningScoreRepository(),
        val pitchingRepo: FakePitchingStatRepository = FakePitchingStatRepository(),
        val battingRepo: FakeBattingStatRepository = FakeBattingStatRepository(),
        val homeRunRepo: FakeHomeRunRepository = FakeHomeRunRepository(),
        val fielderRepo: FakeFielderAppointmentRepository = FakeFielderAppointmentRepository(),
        val pitcherAppRepo: FakePitcherAppointmentRepository = FakePitcherAppointmentRepository(),
    )

    private fun buildViewModel(deps: TestDeps): AfterGameViewModel {
        val scheduleUseCase = GameScheduleUseCase(deps.fixtureRepo, deps.teamRepo)
        val inningUseCase = InningScoreUseCase(deps.inningRepo)
        val pitchingUseCase = PitchingStatUseCase(deps.pitchingRepo)
        val homeRunUseCase = HomeRunUseCase(deps.homeRunRepo)
        val rankingUseCase = RankingUseCase(deps.teamRepo, deps.fixtureRepo, deps.resultRepo)
        val playerInfoUseCase = PlayerInfoUseCase(deps.playerRepo, deps.posRepo, deps.teamRepo, deps.battingRepo, deps.pitchingRepo)
        val gameInfoUseCase = GameInfoUseCase(deps.fixtureRepo, deps.resultRepo, deps.teamRepo)

        val teamUseCase = TeamUseCase(deps.teamRepo, deps.playerRepo, deps.fielderRepo, deps.pitcherAppRepo, deps.posRepo, deps.battingRepo, deps.pitchingRepo)
        val executeGameUseCase = ExecuteGameUseCase(deps.resultRepo, deps.fixtureRepo, deps.teamRepo)
        val battingStatUseCase = BattingStatUseCase(deps.battingRepo)

        val executeGamesByDate = ExecuteGamesByDate(
            executeGameUseCase, teamUseCase, scheduleUseCase,
            battingStatUseCase, pitchingUseCase, inningUseCase, homeRunUseCase,
            object : TransactionProvider {
                override suspend fun <T> runInTransaction(block: suspend () -> T): T = block()
            }
        )

        return AfterGameViewModel(
            scheduleUseCase, executeGamesByDate, inningUseCase, pitchingUseCase,
            homeRunUseCase, rankingUseCase, playerInfoUseCase, gameInfoUseCase
        )
    }

    @Test
    fun setDate_updatesDateInState() {
        val vm = buildViewModel(TestDeps())

        val newDate = LocalDate.of(2025, 5, 1)
        vm.setDate(newDate)

        assertEquals(newDate, vm.uiState.value.date)
    }

    @Test
    fun init_defaultDateIsConstantsStart() {
        val vm = buildViewModel(TestDeps())
        assertEquals(Constants.START, vm.uiState.value.date)
    }

    @Test
    fun init_defaultIsRunningIsFalse() {
        val vm = buildViewModel(TestDeps())
        assertFalse(vm.uiState.value.isRunning)
    }

    @Test
    fun setDate_multipleTimes_retainsLastValue() {
        val vm = buildViewModel(TestDeps())

        vm.setDate(LocalDate.of(2025, 4, 1))
        vm.setDate(LocalDate.of(2025, 5, 15))
        vm.setDate(LocalDate.of(2025, 6, 30))

        assertEquals(LocalDate.of(2025, 6, 30), vm.uiState.value.date)
    }

    @Test
    fun skipGame_withNoTeams_loadEmptyRankingsAndGames() = runTest {
        val vm = buildViewModel(TestDeps())

        vm.skipGame()

        assertTrue(vm.uiState.value.rankings.isEmpty())
        assertTrue(vm.uiState.value.games.isEmpty())
    }

    @Test
    fun skipGame_withTeamsAndResults_loadsRankingsAndGames() = runTest {
        val homeTeam = Team(Constants.TEAM_ID, "Home", League.L1)
        val awayTeam = Team(1, "Away", League.L1)
        val fixture = GameFixture(1, Constants.START, 0, homeTeam.id, awayTeam.id)
        val result = GameResult(1, 3, 1)

        val deps = TestDeps(
            teamRepo = FakeTeamRepository(listOf(homeTeam, awayTeam)),
            fixtureRepo = FakeGameFixtureRepository(listOf(fixture)),
            resultRepo = FakeGameResultRepository(mutableListOf(result)),
        )
        val vm = buildViewModel(deps)

        vm.skipGame()

        // Rankings should be loaded (2 teams in L1)
        assertEquals(2, vm.uiState.value.rankings.size)
        // Games for the date should be loaded
        assertEquals(1, vm.uiState.value.games.size)
    }

    @Test
    fun runGame_withNoSchedules_completesWithoutError() = runTest {
        val vm = buildViewModel(TestDeps())

        vm.runGame()

        // isRunning should be false after completion
        assertFalse(vm.uiState.value.isRunning)
    }

    @Test
    fun initData_withNoSchedule_keepsDefaultState() = runTest {
        val vm = buildViewModel(TestDeps())

        vm.initData()

        assertEquals("", vm.uiState.value.homeTeamName)
        assertEquals("", vm.uiState.value.awayTeamName)
        assertTrue(vm.uiState.value.homeScores.isEmpty())
        assertTrue(vm.uiState.value.awayScores.isEmpty())
    }

    @Test
    fun initData_withScheduleNotMatchingTeamId_keepsDefaultState() = runTest {
        val teamA = Team(10, "A", League.L1)
        val teamB = Team(11, "B", League.L1)
        val fixture = GameFixture(1, Constants.START, 0, teamA.id, teamB.id)

        val deps = TestDeps(
            teamRepo = FakeTeamRepository(listOf(teamA, teamB)),
            fixtureRepo = FakeGameFixtureRepository(listOf(fixture)),
        )
        val vm = buildViewModel(deps)

        vm.initData()

        assertEquals("", vm.uiState.value.homeTeamName)
        assertEquals("", vm.uiState.value.awayTeamName)
    }

    @Test
    fun initData_withMatchingSchedule_loadsGameData() = runTest {
        val homeTeam = Team(Constants.TEAM_ID, "Home", League.L1)
        val awayTeam = Team(1, "Away", League.L1)
        val fixture = GameFixture(1, Constants.START, 0, homeTeam.id, awayTeam.id)
        val result = GameResult(1, 3, 1)

        val homePlayer = mkPlayer(10, homeTeam.id, "HP1")
        val awayPlayer = mkPlayer(20, awayTeam.id, "AP1")
        val players = listOf(homePlayer, awayPlayer)
        val positions = players.map { PlayerPosition(it.id, Position.OUTFIELDER, 10) }

        val inningScores = listOf(
            InningScore(1, homeTeam.id, 1, 2),
            InningScore(1, homeTeam.id, 2, 1),
            InningScore(1, awayTeam.id, 1, 1),
        )

        val pitchingStats = listOf(
            PitchingStat(gameFixtureId = 1, playerId = homePlayer.id, inningPitched = 27, win = true, numberOfPitches = 1),
            PitchingStat(gameFixtureId = 1, playerId = awayPlayer.id, inningPitched = 27, lose = true, numberOfPitches = 1),
        )

        val homeRuns = listOf(
            HomeRun(fixtureId = 1, playerId = homePlayer.id, inning = 1, count = 1),
        )

        val deps = TestDeps(
            teamRepo = FakeTeamRepository(listOf(homeTeam, awayTeam)),
            fixtureRepo = FakeGameFixtureRepository(listOf(fixture)),
            resultRepo = FakeGameResultRepository(mutableListOf(result)),
            playerRepo = FakePlayerRepository(players),
            posRepo = FakePlayerPositionRepository(positions),
            inningRepo = FakeInningScoreRepository(inningScores.toMutableList()),
            pitchingRepo = FakePitchingStatRepository(pitchingStats.toMutableList()),
            homeRunRepo = FakeHomeRunRepository(homeRuns.toMutableList()),
        )
        val vm = buildViewModel(deps)

        vm.initData()

        val state = vm.uiState.value
        assertEquals("Home", state.homeTeamName)
        assertEquals("Away", state.awayTeamName)
        assertEquals(2, state.homeScores.size)
        assertEquals(1, state.awayScores.size)
        assertEquals(1, state.homePitcherResults.size)
        assertEquals(1, state.awayPitcherResults.size)
        assertTrue(state.homePitcherResults[0].isWin)
        assertTrue(state.awayPitcherResults[0].isLoss)
        assertEquals(1, state.homeFielderResults.size)
        assertTrue(state.awayFielderResults.isEmpty())
        assertEquals(2, state.rankings.size)
        assertEquals(1, state.games.size)
    }

    @Test
    fun initData_awayTeamMatchesTeamId_loadsCorrectly() = runTest {
        val homeTeam = Team(1, "Home", League.L1)
        val awayTeam = Team(Constants.TEAM_ID, "Away", League.L1)
        val fixture = GameFixture(1, Constants.START, 0, homeTeam.id, awayTeam.id)
        val result = GameResult(1, 2, 5)

        val deps = TestDeps(
            teamRepo = FakeTeamRepository(listOf(homeTeam, awayTeam)),
            fixtureRepo = FakeGameFixtureRepository(listOf(fixture)),
            resultRepo = FakeGameResultRepository(mutableListOf(result)),
            playerRepo = FakePlayerRepository(emptyList()),
            posRepo = FakePlayerPositionRepository(emptyList()),
        )
        val vm = buildViewModel(deps)

        vm.initData()

        val state = vm.uiState.value
        assertEquals("Home", state.homeTeamName)
        assertEquals("Away", state.awayTeamName)
    }

    @Test
    fun skipGame_afterSetDate_usesUpdatedDate() = runTest {
        val homeTeam = Team(Constants.TEAM_ID, "Home", League.L1)
        val awayTeam = Team(1, "Away", League.L1)
        val day2 = LocalDate.of(2025, 4, 5)
        val fixture = GameFixture(1, day2, 0, homeTeam.id, awayTeam.id)
        val result = GameResult(1, 4, 2)

        val deps = TestDeps(
            teamRepo = FakeTeamRepository(listOf(homeTeam, awayTeam)),
            fixtureRepo = FakeGameFixtureRepository(listOf(fixture)),
            resultRepo = FakeGameResultRepository(mutableListOf(result)),
        )
        val vm = buildViewModel(deps)

        // Default date (Constants.START) has no games
        vm.skipGame()
        assertTrue(vm.uiState.value.games.isEmpty())

        // Set date to day2 which has a game
        vm.setDate(day2)
        vm.skipGame()
        assertEquals(1, vm.uiState.value.games.size)
    }
}
