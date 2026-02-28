package com.example.penasim.ui.game

import com.example.penasim.const.Constants
import com.example.penasim.domain.BattingStat
import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.GameFixture
import com.example.penasim.domain.GameResult
import com.example.penasim.domain.League
import com.example.penasim.domain.OrderType
import com.example.penasim.domain.PitcherAppointment
import com.example.penasim.domain.PitcherType
import com.example.penasim.domain.PitchingStat
import com.example.penasim.domain.Player
import com.example.penasim.domain.PlayerPosition
import com.example.penasim.domain.Position
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.BattingStatRepository
import com.example.penasim.domain.repository.FielderAppointmentRepository
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.GameResultRepository
import com.example.penasim.domain.repository.PitcherAppointmentRepository
import com.example.penasim.domain.repository.PitchingStatRepository
import com.example.penasim.domain.repository.PlayerPositionRepository
import com.example.penasim.domain.repository.PlayerRepository
import com.example.penasim.domain.repository.TeamRepository
import com.example.penasim.testing.MainDispatcherRule
import com.example.penasim.ui.common.GetDisplayFielder
import com.example.penasim.usecase.FielderAppointmentUseCase
import com.example.penasim.usecase.GameScheduleUseCase
import com.example.penasim.usecase.PitcherAppointmentUseCase
import com.example.penasim.usecase.PlayerInfoUseCase
import com.example.penasim.usecase.RankingUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class BeforeGameViewModelTest {
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

    private class FakeGameResultRepository(private val results: List<GameResult>) : GameResultRepository {
        override suspend fun getGameByFixtureId(fixtureId: Int): GameResult? = results.find { it.fixtureId == fixtureId }
        override suspend fun getGamesByFixtureIds(fixtureIds: List<Int>): List<GameResult> = results.filter { it.fixtureId in fixtureIds }
        override suspend fun getAllGames(): List<GameResult> = results
        override suspend fun deleteAllGames() {}
        override suspend fun createGame(fixtureId: Int, homeScore: Int, awayScore: Int): GameResult? = null
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

    private class EmptyBattingStatRepository : BattingStatRepository {
        override suspend fun getByFixtureId(fixtureId: Int) = emptyList<BattingStat>()
        override suspend fun getByFixtureIds(fixtureIds: List<Int>) = emptyList<BattingStat>()
        override suspend fun getByPlayerId(playerId: Int) = emptyList<BattingStat>()
        override suspend fun getByPlayerIds(playerIds: List<Int>) = emptyList<BattingStat>()
        override suspend fun insertAll(items: List<BattingStat>) {}
        override suspend fun deleteByFixtureId(fixtureId: Int) {}
    }

    private class EmptyPitchingStatRepository : PitchingStatRepository {
        override suspend fun getByFixtureId(fixtureId: Int) = emptyList<PitchingStat>()
        override suspend fun getByFixtureIds(fixtureIds: List<Int>) = emptyList<PitchingStat>()
        override suspend fun getByPlayerId(playerId: Int) = emptyList<PitchingStat>()
        override suspend fun getByPlayerIds(playerIds: List<Int>) = emptyList<PitchingStat>()
        override suspend fun insertAll(items: List<PitchingStat>) {}
        override suspend fun deleteByFixtureId(fixtureId: Int) {}
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

    private fun buildViewModel(
        teams: List<Team>,
        fixtures: List<GameFixture>,
        results: List<GameResult>,
        players: List<Player>,
        positions: List<PlayerPosition>,
        fielderAppointments: List<FielderAppointment>,
        pitcherAppointments: List<PitcherAppointment>,
    ): BeforeGameViewModel {
        val teamRepo = FakeTeamRepository(teams)
        val fixtureRepo = FakeGameFixtureRepository(fixtures)
        val resultRepo = FakeGameResultRepository(results)
        val playerRepo = FakePlayerRepository(players)
        val posRepo = FakePlayerPositionRepository(positions)
        val battingRepo = EmptyBattingStatRepository()
        val pitchingRepo = EmptyPitchingStatRepository()
        val fielderRepo = FakeFielderAppointmentRepository(fielderAppointments)
        val pitcherRepo = FakePitcherAppointmentRepository(pitcherAppointments)

        val rankingUseCase = RankingUseCase(teamRepo, fixtureRepo, resultRepo)
        val scheduleUseCase = GameScheduleUseCase(fixtureRepo, teamRepo)
        val playerInfoUseCase = PlayerInfoUseCase(playerRepo, posRepo, teamRepo, battingRepo, pitchingRepo)
        val fielderUseCase = FielderAppointmentUseCase(fielderRepo)
        val pitcherUseCase = PitcherAppointmentUseCase(pitcherRepo)
        val getDisplayFielder = GetDisplayFielder(playerInfoUseCase, fielderUseCase, pitcherUseCase)

        return BeforeGameViewModel(rankingUseCase, scheduleUseCase, getDisplayFielder)
    }

    @Test
    fun setDate_updatesDateInState() {
        val vm = buildViewModel(
            teams = emptyList(),
            fixtures = emptyList(),
            results = emptyList(),
            players = emptyList(),
            positions = emptyList(),
            fielderAppointments = emptyList(),
            pitcherAppointments = emptyList(),
        )

        val newDate = LocalDate.of(2025, 5, 1)
        vm.setDate(newDate)

        assertEquals(newDate, vm.uiState.value.date)
    }

    @Test
    fun init_defaultDateIsConstantsStart() {
        val vm = buildViewModel(
            teams = emptyList(),
            fixtures = emptyList(),
            results = emptyList(),
            players = emptyList(),
            positions = emptyList(),
            fielderAppointments = emptyList(),
            pitcherAppointments = emptyList(),
        )

        assertEquals(Constants.START, vm.uiState.value.date)
    }

    @Test
    fun init_withNoSchedulesForDate_keepsDefaultState() = runTest {
        val vm = buildViewModel(
            teams = emptyList(),
            fixtures = emptyList(),
            results = emptyList(),
            players = emptyList(),
            positions = emptyList(),
            fielderAppointments = emptyList(),
            pitcherAppointments = emptyList(),
        )

        // No schedule found → init returns early, keeping defaults
        assertTrue(vm.uiState.value.homeStartingPlayers.isEmpty())
        assertTrue(vm.uiState.value.awayStartingPlayers.isEmpty())
        assertEquals(0, vm.uiState.value.homeTeam.rank)
        assertEquals(0, vm.uiState.value.awayTeam.rank)
    }

    @Test
    fun init_withScheduleNotMatchingTeamId_keepsDefaultState() = runTest {
        val teamA = Team(10, "A", League.L1)
        val teamB = Team(11, "B", League.L1)
        val fixture = GameFixture(1, Constants.START, 0, teamA.id, teamB.id)

        val vm = buildViewModel(
            teams = listOf(teamA, teamB),
            fixtures = listOf(fixture),
            results = emptyList(),
            players = emptyList(),
            positions = emptyList(),
            fielderAppointments = emptyList(),
            pitcherAppointments = emptyList(),
        )

        // Schedule exists but neither team matches Constants.TEAM_ID (0)
        assertTrue(vm.uiState.value.homeStartingPlayers.isEmpty())
        assertTrue(vm.uiState.value.awayStartingPlayers.isEmpty())
    }

    @Test
    fun init_withMatchingSchedule_loadsRankingsAndStartingPlayers() = runTest {
        val homeTeam = Team(Constants.TEAM_ID, "Home", League.L1)
        val awayTeam = Team(1, "Away", League.L1)
        val fixture = GameFixture(1, Constants.START, 0, homeTeam.id, awayTeam.id)

        val homePlayer1 = mkPlayer(10, homeTeam.id, "HP1")
        val homePlayer2 = mkPlayer(11, homeTeam.id, "HP2")
        val homePitcher = mkPlayer(12, homeTeam.id, "HPitcher")
        val awayPlayer1 = mkPlayer(20, awayTeam.id, "AP1")
        val awayPlayer2 = mkPlayer(21, awayTeam.id, "AP2")
        val awayPitcher = mkPlayer(22, awayTeam.id, "APitcher")

        val players = listOf(homePlayer1, homePlayer2, homePitcher, awayPlayer1, awayPlayer2, awayPitcher)
        val positions = players.map { PlayerPosition(it.id, Position.OUTFIELDER, 10) }

        val fielderAppointments = listOf(
            FielderAppointment(homeTeam.id, homePlayer1.id, Position.OUTFIELDER, 1, OrderType.NORMAL),
            FielderAppointment(homeTeam.id, homePlayer2.id, Position.CATCHER, 2, OrderType.NORMAL),
            FielderAppointment(homeTeam.id, homePitcher.id, Position.PITCHER, 3, OrderType.NORMAL),
            FielderAppointment(awayTeam.id, awayPlayer1.id, Position.OUTFIELDER, 1, OrderType.NORMAL),
            FielderAppointment(awayTeam.id, awayPlayer2.id, Position.CATCHER, 2, OrderType.NORMAL),
            FielderAppointment(awayTeam.id, awayPitcher.id, Position.PITCHER, 3, OrderType.NORMAL),
        )

        val pitcherAppointments = listOf(
            PitcherAppointment(homeTeam.id, homePitcher.id, PitcherType.STARTER, 1),
            PitcherAppointment(awayTeam.id, awayPitcher.id, PitcherType.STARTER, 1),
        )

        val vm = buildViewModel(
            teams = listOf(homeTeam, awayTeam),
            fixtures = listOf(fixture),
            results = emptyList(),
            players = players,
            positions = positions,
            fielderAppointments = fielderAppointments,
            pitcherAppointments = pitcherAppointments,
        )

        val state = vm.uiState.value
        assertEquals(homeTeam.id, state.homeTeam.team.id)
        assertEquals(awayTeam.id, state.awayTeam.team.id)
        // Starting players loaded (3 per team: 2 fielders + pitcher replaced by starter)
        assertEquals(3, state.homeStartingPlayers.size)
        assertEquals(3, state.awayStartingPlayers.size)
    }

    @Test
    fun init_withMatchingSchedule_rankingsReflectWinsLosses() = runTest {
        val homeTeam = Team(Constants.TEAM_ID, "Home", League.L1)
        val awayTeam = Team(1, "Away", League.L1)
        val date = Constants.START
        val prevDate = date.minusDays(1)

        val prevFixture = GameFixture(0, prevDate, 0, homeTeam.id, awayTeam.id)
        val todayFixture = GameFixture(1, date, 0, homeTeam.id, awayTeam.id)

        val results = listOf(GameResult(0, 5, 2)) // homeTeam wins

        val homePlayer = mkPlayer(10, homeTeam.id, "HP1")
        val awayPlayer = mkPlayer(20, awayTeam.id, "AP1")
        val players = listOf(homePlayer, awayPlayer)
        val positions = players.map { PlayerPosition(it.id, Position.OUTFIELDER, 10) }

        val fielderAppointments = listOf(
            FielderAppointment(homeTeam.id, homePlayer.id, Position.OUTFIELDER, 1, OrderType.NORMAL),
            FielderAppointment(awayTeam.id, awayPlayer.id, Position.OUTFIELDER, 1, OrderType.NORMAL),
        )

        val vm = buildViewModel(
            teams = listOf(homeTeam, awayTeam),
            fixtures = listOf(prevFixture, todayFixture),
            results = results,
            players = players,
            positions = positions,
            fielderAppointments = fielderAppointments,
            pitcherAppointments = emptyList(),
        )

        val state = vm.uiState.value
        assertEquals(1, state.homeTeam.rank)
        assertEquals(1, state.homeTeam.wins)
        assertEquals(0, state.homeTeam.losses)
        assertEquals(2, state.awayTeam.rank)
        assertEquals(0, state.awayTeam.wins)
        assertEquals(1, state.awayTeam.losses)
    }

    @Test
    fun init_awayTeamMatchesTeamId_loadsCorrectly() = runTest {
        val homeTeam = Team(1, "Home", League.L1)
        val awayTeam = Team(Constants.TEAM_ID, "Away", League.L1)
        val fixture = GameFixture(1, Constants.START, 0, homeTeam.id, awayTeam.id)

        val homePlayer = mkPlayer(10, homeTeam.id, "HP1")
        val awayPlayer = mkPlayer(20, awayTeam.id, "AP1")
        val players = listOf(homePlayer, awayPlayer)
        val positions = players.map { PlayerPosition(it.id, Position.OUTFIELDER, 10) }

        val fielderAppointments = listOf(
            FielderAppointment(homeTeam.id, homePlayer.id, Position.OUTFIELDER, 1, OrderType.NORMAL),
            FielderAppointment(awayTeam.id, awayPlayer.id, Position.OUTFIELDER, 1, OrderType.NORMAL),
        )

        val vm = buildViewModel(
            teams = listOf(homeTeam, awayTeam),
            fixtures = listOf(fixture),
            results = emptyList(),
            players = players,
            positions = positions,
            fielderAppointments = fielderAppointments,
            pitcherAppointments = emptyList(),
        )

        val state = vm.uiState.value
        assertEquals(homeTeam.id, state.homeTeam.team.id)
        assertEquals(awayTeam.id, state.awayTeam.team.id)
    }

    @Test
    fun setDate_multipleTimes_retainsLastValue() {
        val vm = buildViewModel(
            teams = emptyList(),
            fixtures = emptyList(),
            results = emptyList(),
            players = emptyList(),
            positions = emptyList(),
            fielderAppointments = emptyList(),
            pitcherAppointments = emptyList(),
        )

        vm.setDate(LocalDate.of(2025, 4, 1))
        vm.setDate(LocalDate.of(2025, 5, 15))
        vm.setDate(LocalDate.of(2025, 6, 30))

        assertEquals(LocalDate.of(2025, 6, 30), vm.uiState.value.date)
    }
}
