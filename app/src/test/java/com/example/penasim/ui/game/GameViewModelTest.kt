package com.example.penasim.ui.game

import com.example.penasim.const.Constants
import com.example.penasim.domain.*
import com.example.penasim.domain.repository.*
import com.example.penasim.game.ExecuteGamesByDate
import com.example.penasim.testing.MainDispatcherRule
import com.example.penasim.usecase.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class GameViewModelTest {
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
        override suspend fun createGame(fixtureId: Int, homeScore: Int, awayScore: Int): GameResult? = GameResult(fixtureId, homeScore, awayScore)
    }

    private fun mkPlayer(id: Int, teamId: Int, firstName: String): Player = Player(
        id = id,
        firstName = firstName,
        lastName = "",
        teamId = teamId,
        meet = 50,
        power = 50,
        speed = 50,
        throwing = 50,
        defense = 50,
        catching = 50,
        ballSpeed = 140,
        control = 50,
        stamina = 50,
        starter = 50,
        reliever = 50,
    )

    @Test
    fun init_withSchedule_buildsBeforeGameInfo_andPlayers() = runTest {
        val date = Constants.START
        val home = Team(0, "Home", League.L1)
        val away = Team(1, "Away", League.L1)
        val fixture = GameFixture(10, date, 1, home.id, away.id)
        val result = GameResult(10, 3, 1)

        val teams = listOf(home, away)
        val teamRepo = FakeTeamRepository(teams)
        val fixtureRepo = FakeGameFixtureRepository(listOf(fixture))
        val resultRepo = FakeGameResultRepository(listOf(result))

        val rankingUseCase = RankingUseCase(teamRepo, fixtureRepo, resultRepo)
        val scheduleUseCase = GameScheduleUseCase(fixtureRepo, teamRepo)

        val fielderRepo = object : FielderAppointmentRepository {
            private val data = listOf(
                FielderAppointment(home.id, 100, Position.OUTFIELDER, 1, OrderType.NORMAL),
                FielderAppointment(home.id, 101, Position.CATCHER, 2, OrderType.NORMAL),
                FielderAppointment(away.id, 200, Position.OUTFIELDER, 1, OrderType.NORMAL),
                FielderAppointment(away.id, 201, Position.CATCHER, 2, OrderType.NORMAL),
            )
            override suspend fun getFielderAppointmentByPlayerId(playerId: Int) = data.find { it.playerId == playerId }
            override suspend fun getFielderAppointmentsByTeamId(teamId: Int) = data.filter { it.teamId == teamId }
            override suspend fun insertFielderAppointment(fielderAppointment: FielderAppointment) {}
            override suspend fun insertFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
            override suspend fun deleteFielderAppointment(fielderAppointment: FielderAppointment) {}
            override suspend fun deleteFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
            override suspend fun updateFielderAppointment(fielderAppointment: FielderAppointment) {}
            override suspend fun updateFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
        }
        val fielderUseCase = FielderAppointmentUseCase(fielderRepo)

        val playerRepo = object : PlayerRepository {
            override suspend fun getPlayerCount(teamId: Int): Int = 2
            override suspend fun getPlayers(teamId: Int): List<Player> = when (teamId) {
                home.id -> listOf(mkPlayer(100, teamId, "H100"), mkPlayer(101, teamId, "H101"))
                away.id -> listOf(mkPlayer(200, teamId, "A200"), mkPlayer(201, teamId, "A201"))
                else -> emptyList()
            }
            override suspend fun getPlayer(id: Int): Player? = null
            override suspend fun getAllPlayers(): List<Player> = emptyList()
        }
        val posRepo = object : PlayerPositionRepository {
            override suspend fun getPlayerPositions(playerId: Int): List<PlayerPosition> = listOf(
                PlayerPosition(playerId, Position.OUTFIELDER, 10)
            )
            override suspend fun getAllPlayerPositions(): List<PlayerPosition> = emptyList()
            override suspend fun getAllPlayerPositionsByPosition(position: Position): List<PlayerPosition> = emptyList()
        }
        val batRepo = object : BattingStatRepository {
            override suspend fun getByFixtureId(fixtureId: Int) = emptyList<BattingStat>()
            override suspend fun getByFixtureIds(fixtureIds: List<Int>) = emptyList<BattingStat>()
            override suspend fun getByPlayerId(playerId: Int) = emptyList<BattingStat>()
            override suspend fun getByPlayerIds(playerIds: List<Int>) = emptyList<BattingStat>()
            override suspend fun insertAll(items: List<BattingStat>) {}
            override suspend fun deleteByFixtureId(fixtureId: Int) {}
        }
        val pitRepo = object : PitchingStatRepository {
            override suspend fun getByFixtureId(fixtureId: Int) = emptyList<PitchingStat>()
            override suspend fun getByFixtureIds(fixtureIds: List<Int>) = emptyList<PitchingStat>()
            override suspend fun getByPlayerId(playerId: Int) = emptyList<PitchingStat>()
            override suspend fun getByPlayerIds(playerIds: List<Int>) = emptyList<PitchingStat>()
            override suspend fun insertAll(items: List<PitchingStat>) {}
            override suspend fun deleteByFixtureId(fixtureId: Int) {}
        }
        val playerInfoUseCase = PlayerInfoUseCase(playerRepo, posRepo, teamRepo, batRepo, pitRepo)

        val pitchingUseCase = PitchingStatUseCase(pitRepo)
        val inningUseCase = InningScoreUseCase(object : InningScoreRepository {
            override suspend fun getByFixtureId(fixtureId: Int) = emptyList<InningScore>()
            override suspend fun getByFixtureIds(fixtureIds: List<Int>) = emptyList<InningScore>()
            override suspend fun getByTeamId(teamId: Int) = emptyList<InningScore>()
            override suspend fun getByTeamIds(teamIds: List<Int>) = emptyList<InningScore>()
            override suspend fun insertAll(items: List<InningScore>) {}
            override suspend fun deleteByFixtureId(fixtureId: Int) {}
        })
        val homeRunUseCase = HomeRunUseCase(object : HomeRunRepository {
            override suspend fun getHomeRunsByFixtureId(fixtureId: Int) = emptyList<HomeRun>()
            override suspend fun getHomeRunsByPlayerId(playerId: Int) = emptyList<HomeRun>()
            override suspend fun insertHomeRuns(homeRuns: List<HomeRun>) {}
        })

        val executeGameUseCase = ExecuteGameUseCase(resultRepo, fixtureRepo, teamRepo)
        val executor = ExecuteGamesByDate(
            executeGameUseCase,
            TeamUseCase(
                teamRepo,
                playerRepo,
                object : FielderAppointmentRepository {
                    override suspend fun getFielderAppointmentByPlayerId(playerId: Int) = null
                    override suspend fun getFielderAppointmentsByTeamId(teamId: Int) = emptyList<FielderAppointment>()
                    override suspend fun insertFielderAppointment(fielderAppointment: FielderAppointment) {}
                    override suspend fun insertFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
                    override suspend fun deleteFielderAppointment(fielderAppointment: FielderAppointment) {}
                    override suspend fun deleteFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
                    override suspend fun updateFielderAppointment(fielderAppointment: FielderAppointment) {}
                    override suspend fun updateFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
                },
                object : PitcherAppointmentRepository {
                    override suspend fun getPitcherAppointmentsByTeamId(teamId: Int) = emptyList<PitcherAppointment>()
                    override suspend fun getPitcherAppointmentByPlayerId(playerId: Int) = null
                    override suspend fun insertPitcherAppointment(pitcherAppointment: PitcherAppointment) {}
                    override suspend fun insertPitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {}
                    override suspend fun deletePitcherAppointment(pitcherAppointment: PitcherAppointment) {}
                    override suspend fun deletePitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {}
                    override suspend fun updatePitcherAppointment(pitcherAppointment: PitcherAppointment) {}
                    override suspend fun updatePitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {}
                },
                posRepo,
                batRepo,
                pitRepo
            ),
            scheduleUseCase,
            BattingStatUseCase(batRepo),
            pitchingUseCase,
            inningUseCase,
            homeRunUseCase,
            object : TransactionProvider { override suspend fun <T> runInTransaction(block: suspend () -> T): T = block() }
        )

        val vm = GameViewModel(
            executor,
            inningUseCase,
            pitchingUseCase,
            fielderUseCase,
            rankingUseCase,
            scheduleUseCase,
            playerInfoUseCase,
            homeRunUseCase
        )
        vm.setDate(date)

        // Assert initial before-game info populated
        val state = vm.uiState.value
        assertEquals(2, state.homePlayers.size)
        assertTrue(state.beforeGameInfo.homeStartingPlayers.isNotEmpty())
        assertTrue(state.beforeGameInfo.awayStartingPlayers.isNotEmpty())
        assertEquals("H100", state.beforeGameInfo.homeStartingPlayers[0].displayName)
    }

    @Test
    fun init_withoutSchedule_callsSkipGame_andClearsBeforeGameInfo() = runTest {
        val date = Constants.START
        val teams = listOf(Team(0, "Home", League.L1), Team(1, "Away", League.L1))
        val teamRepo = FakeTeamRepository(teams)
        val fixtureRepo = FakeGameFixtureRepository(emptyList())
        val resultRepo = FakeGameResultRepository(emptyList())

        val rankingUseCase = RankingUseCase(teamRepo, fixtureRepo, resultRepo)
        val scheduleUseCase = GameScheduleUseCase(fixtureRepo, teamRepo)
        val fielderUseCase = FielderAppointmentUseCase(object : FielderAppointmentRepository {
            override suspend fun getFielderAppointmentByPlayerId(playerId: Int) = null
            override suspend fun getFielderAppointmentsByTeamId(teamId: Int) = emptyList<FielderAppointment>()
            override suspend fun insertFielderAppointment(fielderAppointment: FielderAppointment) {}
            override suspend fun insertFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
            override suspend fun deleteFielderAppointment(fielderAppointment: FielderAppointment) {}
            override suspend fun deleteFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
            override suspend fun updateFielderAppointment(fielderAppointment: FielderAppointment) {}
            override suspend fun updateFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
        })
        val playerRepo = object : PlayerRepository {
            override suspend fun getPlayerCount(teamId: Int): Int = 0
            override suspend fun getPlayers(teamId: Int) = emptyList<Player>()
            override suspend fun getPlayer(id: Int) = null
            override suspend fun getAllPlayers(): List<Player> = emptyList()
        }
        val playerInfoUseCase = PlayerInfoUseCase(
            playerRepo,
            object : PlayerPositionRepository {
                override suspend fun getPlayerPositions(playerId: Int) = emptyList<PlayerPosition>()
                override suspend fun getAllPlayerPositions(): List<PlayerPosition> = emptyList()
                override suspend fun getAllPlayerPositionsByPosition(position: Position): List<PlayerPosition> = emptyList()
            },
            teamRepo,
            object : BattingStatRepository { override suspend fun getByFixtureId(fixtureId: Int)= emptyList<BattingStat>(); override suspend fun getByFixtureIds(fixtureIds: List<Int>)= emptyList<BattingStat>(); override suspend fun getByPlayerId(playerId: Int)= emptyList<BattingStat>(); override suspend fun getByPlayerIds(playerIds: List<Int>)= emptyList<BattingStat>(); override suspend fun insertAll(items: List<BattingStat>) {}; override suspend fun deleteByFixtureId(fixtureId: Int) {} },
            object : PitchingStatRepository { override suspend fun getByFixtureId(fixtureId: Int)= emptyList<PitchingStat>(); override suspend fun getByFixtureIds(fixtureIds: List<Int>)= emptyList<PitchingStat>(); override suspend fun getByPlayerId(playerId: Int)= emptyList<PitchingStat>(); override suspend fun getByPlayerIds(playerIds: List<Int>)= emptyList<PitchingStat>(); override suspend fun insertAll(items: List<PitchingStat>) {}; override suspend fun deleteByFixtureId(fixtureId: Int) {} },
        )
        val pitchingUseCase = PitchingStatUseCase(object : PitchingStatRepository { override suspend fun getByFixtureId(fixtureId: Int)= emptyList<PitchingStat>(); override suspend fun getByFixtureIds(fixtureIds: List<Int>)= emptyList<PitchingStat>(); override suspend fun getByPlayerId(playerId: Int)= emptyList<PitchingStat>(); override suspend fun getByPlayerIds(playerIds: List<Int>)= emptyList<PitchingStat>(); override suspend fun insertAll(items: List<PitchingStat>) {}; override suspend fun deleteByFixtureId(fixtureId: Int) {} })
        val inningUseCase = InningScoreUseCase(object : InningScoreRepository { override suspend fun getByFixtureId(fixtureId: Int)= emptyList<InningScore>(); override suspend fun getByFixtureIds(fixtureIds: List<Int>)= emptyList<InningScore>(); override suspend fun getByTeamId(teamId: Int)= emptyList<InningScore>(); override suspend fun getByTeamIds(teamIds: List<Int>)= emptyList<InningScore>(); override suspend fun insertAll(items: List<InningScore>) {}; override suspend fun deleteByFixtureId(fixtureId: Int) {} })
        val homeRunUseCase = HomeRunUseCase(object : HomeRunRepository { override suspend fun getHomeRunsByFixtureId(fixtureId: Int)= emptyList<HomeRun>(); override suspend fun getHomeRunsByPlayerId(playerId: Int)= emptyList<HomeRun>(); override suspend fun insertHomeRuns(homeRuns: List<HomeRun>) {} })
        val executeGameUseCase = ExecuteGameUseCase(resultRepo, fixtureRepo, teamRepo)
        val executor = ExecuteGamesByDate(executeGameUseCase, TeamUseCase(teamRepo,
            playerRepo,
            object : FielderAppointmentRepository { override suspend fun getFielderAppointmentByPlayerId(playerId: Int)= null; override suspend fun getFielderAppointmentsByTeamId(teamId: Int)= emptyList<FielderAppointment>(); override suspend fun insertFielderAppointment(fielderAppointment: FielderAppointment) {}; override suspend fun insertFielderAppointments(fielderAppointments: List<FielderAppointment>) {}; override suspend fun deleteFielderAppointment(fielderAppointment: FielderAppointment) {}; override suspend fun deleteFielderAppointments(fielderAppointments: List<FielderAppointment>) {}; override suspend fun updateFielderAppointment(fielderAppointment: FielderAppointment) {}; override suspend fun updateFielderAppointments(fielderAppointments: List<FielderAppointment>) {} },
            object : PitcherAppointmentRepository { override suspend fun getPitcherAppointmentsByTeamId(teamId: Int)= emptyList<PitcherAppointment>(); override suspend fun getPitcherAppointmentByPlayerId(playerId: Int) = null; override suspend fun insertPitcherAppointment(pitcherAppointment: PitcherAppointment) {}; override suspend fun insertPitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {}; override suspend fun deletePitcherAppointment(pitcherAppointment: PitcherAppointment) {}; override suspend fun deletePitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {}; override suspend fun updatePitcherAppointment(pitcherAppointment: PitcherAppointment) {}; override suspend fun updatePitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {} },
            object : PlayerPositionRepository { override suspend fun getPlayerPositions(playerId: Int)= emptyList<PlayerPosition>(); override suspend fun getAllPlayerPositions(): List<PlayerPosition> = emptyList(); override suspend fun getAllPlayerPositionsByPosition(position: Position): List<PlayerPosition> = emptyList() },
            object : BattingStatRepository { override suspend fun getByFixtureId(fixtureId: Int)= emptyList<BattingStat>(); override suspend fun getByFixtureIds(fixtureIds: List<Int>)= emptyList<BattingStat>(); override suspend fun getByPlayerId(playerId: Int)= emptyList<BattingStat>(); override suspend fun getByPlayerIds(playerIds: List<Int>)= emptyList<BattingStat>(); override suspend fun insertAll(items: List<BattingStat>) {}; override suspend fun deleteByFixtureId(fixtureId: Int) {} },
            object : PitchingStatRepository { override suspend fun getByFixtureId(fixtureId: Int)= emptyList<PitchingStat>(); override suspend fun getByFixtureIds(fixtureIds: List<Int>)= emptyList<PitchingStat>(); override suspend fun getByPlayerId(playerId: Int)= emptyList<PitchingStat>(); override suspend fun getByPlayerIds(playerIds: List<Int>)= emptyList<PitchingStat>(); override suspend fun insertAll(items: List<PitchingStat>) {}; override suspend fun deleteByFixtureId(fixtureId: Int) {} }
        ), scheduleUseCase, BattingStatUseCase(object : BattingStatRepository { override suspend fun getByFixtureId(fixtureId: Int)= emptyList<BattingStat>(); override suspend fun getByFixtureIds(fixtureIds: List<Int>)= emptyList<BattingStat>(); override suspend fun getByPlayerId(playerId: Int)= emptyList<BattingStat>(); override suspend fun getByPlayerIds(playerIds: List<Int>)= emptyList<BattingStat>(); override suspend fun insertAll(items: List<BattingStat>) {}; override suspend fun deleteByFixtureId(fixtureId: Int) {} }), pitchingUseCase, inningUseCase, homeRunUseCase, object : TransactionProvider { override suspend fun <T> runInTransaction(block: suspend () -> T): T = block() })

        val vm = GameViewModel(executor, inningUseCase, pitchingUseCase, fielderUseCase, rankingUseCase, scheduleUseCase, playerInfoUseCase, homeRunUseCase)
        vm.setDate(date)

        val state = vm.uiState.value
        // since there is no schedule for my team, skipGame() should set beforeGameInfo empty and afterGameInfo.games empty
        assertTrue(state.beforeGameInfo.homeStartingPlayers.isEmpty())
        assertTrue(state.afterGameInfo.games.isEmpty())
    }
}
