package com.example.penasim.ui.game

import com.example.penasim.const.Constants
import com.example.penasim.domain.GameFixture
import com.example.penasim.domain.GameInfo
import com.example.penasim.domain.GameResult
import com.example.penasim.domain.GameSchedule
import com.example.penasim.domain.HomeRun
import com.example.penasim.domain.InningScore
import com.example.penasim.domain.League
import com.example.penasim.domain.PitchingStat
import com.example.penasim.domain.Player
import com.example.penasim.domain.PlayerInfo
import com.example.penasim.domain.PlayerPosition
import com.example.penasim.domain.Position
import com.example.penasim.domain.Team
import com.example.penasim.domain.TeamStanding
import com.example.penasim.domain.TotalBattingStats
import com.example.penasim.domain.TotalPitchingStats
import com.example.penasim.game.ExecuteGamesByDate
import com.example.penasim.testing.MainDispatcherRule
import com.example.penasim.usecase.GameInfoUseCase
import com.example.penasim.usecase.GameScheduleUseCase
import com.example.penasim.usecase.HomeRunUseCase
import com.example.penasim.usecase.InningScoreUseCase
import com.example.penasim.usecase.PitchingStatUseCase
import com.example.penasim.usecase.PlayerInfoUseCase
import com.example.penasim.usecase.RankingUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate

class AfterGameViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val gameScheduleUseCase: GameScheduleUseCase = mock()
    private val executeGamesByDate: ExecuteGamesByDate = mock()
    private val inningScoreUseCase: InningScoreUseCase = mock()
    private val pitchingScoreUseCase: PitchingStatUseCase = mock()
    private val homeRunUseCase: HomeRunUseCase = mock()
    private val rankingUseCase: RankingUseCase = mock()
    private val playerInfoUseCase: PlayerInfoUseCase = mock()
    private val gameInfoUseCase: GameInfoUseCase = mock()

    private fun buildViewModel(): AfterGameViewModel = AfterGameViewModel(
        gameScheduleUseCase, executeGamesByDate, inningScoreUseCase, pitchingScoreUseCase,
        homeRunUseCase, rankingUseCase, playerInfoUseCase, gameInfoUseCase
    )

    private fun mkPlayer(id: Int, teamId: Int, name: String): Player = Player(
        id = id, firstName = name, lastName = "", teamId = teamId,
        meet = 50, power = 50, speed = 50, throwing = 50, defense = 50, catching = 50,
        ballSpeed = 140, control = 50, stamina = 50, starter = 50, reliever = 50
    )

    private fun mkPlayerInfo(player: Player): PlayerInfo = PlayerInfo(
        player = player,
        positions = listOf(PlayerPosition(player.id, Position.OUTFIELDER, 10)),
        team = Team(player.teamId, "", League.L1),
        battingStat = TotalBattingStats(playerId = player.id),
        pitchingStat = TotalPitchingStats(playerId = player.id),
    )

    @Test
    fun setDate_updatesDateInState() {
        val vm = buildViewModel()

        val newDate = LocalDate.of(2025, 5, 1)
        vm.setDate(newDate)

        assertEquals(newDate, vm.uiState.value.date)
    }

    @Test
    fun init_defaultDateIsConstantsStart() {
        val vm = buildViewModel()
        assertEquals(Constants.START, vm.uiState.value.date)
    }

    @Test
    fun init_defaultIsRunningIsFalse() {
        val vm = buildViewModel()
        assertFalse(vm.uiState.value.isRunning)
    }

    @Test
    fun setDate_multipleTimes_retainsLastValue() {
        val vm = buildViewModel()

        vm.setDate(LocalDate.of(2025, 4, 1))
        vm.setDate(LocalDate.of(2025, 5, 15))
        vm.setDate(LocalDate.of(2025, 6, 30))

        assertEquals(LocalDate.of(2025, 6, 30), vm.uiState.value.date)
    }

    @Test
    fun skipGame_withNoTeams_loadEmptyRankingsAndGames() = runTest {
        whenever(rankingUseCase.getAll()).thenReturn(emptyList())
        whenever(gameInfoUseCase.getByDate(any())).thenReturn(emptyList())

        val vm = buildViewModel()
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

        whenever(rankingUseCase.getAll()).thenReturn(
            listOf(
                TeamStanding(team = homeTeam, rank = 1, wins = 1),
                TeamStanding(team = awayTeam, rank = 2, losses = 1),
            )
        )
        whenever(gameInfoUseCase.getByDate(Constants.START)).thenReturn(
            listOf(GameInfo(fixture, homeTeam, awayTeam, result))
        )

        val vm = buildViewModel()
        vm.skipGame()

        // Rankings should be loaded (2 teams)
        assertEquals(2, vm.uiState.value.rankings.size)
        // Games for the date should be loaded
        assertEquals(1, vm.uiState.value.games.size)
    }

    @Test
    fun runGame_withNoSchedules_completesWithoutError() = runTest {
        whenever(executeGamesByDate.execute(any())).thenReturn(emptyList())

        val vm = buildViewModel()
        vm.runGame()

        // isRunning should be false after completion
        assertFalse(vm.uiState.value.isRunning)
    }

    @Test
    fun initData_withNoSchedule_keepsDefaultState() = runTest {
        whenever(gameScheduleUseCase.getByDate(any())).thenReturn(emptyList())

        val vm = buildViewModel()
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

        whenever(gameScheduleUseCase.getByDate(Constants.START)).thenReturn(
            listOf(GameSchedule(fixture, teamA, teamB))
        )

        val vm = buildViewModel()
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
        val homePlayerInfo = mkPlayerInfo(homePlayer)
        val awayPlayerInfo = mkPlayerInfo(awayPlayer)

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

        whenever(gameScheduleUseCase.getByDate(Constants.START)).thenReturn(
            listOf(GameSchedule(fixture, homeTeam, awayTeam))
        )
        whenever(playerInfoUseCase.getByTeamId(homeTeam.id)).thenReturn(listOf(homePlayerInfo))
        whenever(playerInfoUseCase.getByTeamId(awayTeam.id)).thenReturn(listOf(awayPlayerInfo))
        whenever(inningScoreUseCase.getByFixtureId(1)).thenReturn(inningScores)
        whenever(pitchingScoreUseCase.getByFixtureId(1)).thenReturn(pitchingStats)
        whenever(homeRunUseCase.getByFixtureId(1)).thenReturn(homeRuns)
        whenever(rankingUseCase.getAll()).thenReturn(
            listOf(
                TeamStanding(team = homeTeam, rank = 1, wins = 1),
                TeamStanding(team = awayTeam, rank = 2, losses = 1),
            )
        )
        whenever(gameInfoUseCase.getByDate(Constants.START)).thenReturn(
            listOf(GameInfo(fixture, homeTeam, awayTeam, result))
        )

        val vm = buildViewModel()
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

        whenever(gameScheduleUseCase.getByDate(Constants.START)).thenReturn(
            listOf(GameSchedule(fixture, homeTeam, awayTeam))
        )
        whenever(playerInfoUseCase.getByTeamId(homeTeam.id)).thenReturn(emptyList())
        whenever(playerInfoUseCase.getByTeamId(awayTeam.id)).thenReturn(emptyList())
        whenever(inningScoreUseCase.getByFixtureId(1)).thenReturn(emptyList())
        whenever(pitchingScoreUseCase.getByFixtureId(1)).thenReturn(emptyList())
        whenever(homeRunUseCase.getByFixtureId(1)).thenReturn(emptyList())
        whenever(rankingUseCase.getAll()).thenReturn(emptyList())
        whenever(gameInfoUseCase.getByDate(Constants.START)).thenReturn(emptyList())

        val vm = buildViewModel()
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

        whenever(rankingUseCase.getAll()).thenReturn(emptyList())
        whenever(gameInfoUseCase.getByDate(Constants.START)).thenReturn(emptyList())
        whenever(gameInfoUseCase.getByDate(day2)).thenReturn(
            listOf(GameInfo(fixture, homeTeam, awayTeam, result))
        )

        val vm = buildViewModel()

        // Default date (Constants.START) has no games
        vm.skipGame()
        assertTrue(vm.uiState.value.games.isEmpty())

        // Set date to day2 which has a game
        vm.setDate(day2)
        vm.skipGame()
        assertEquals(1, vm.uiState.value.games.size)
    }
}
