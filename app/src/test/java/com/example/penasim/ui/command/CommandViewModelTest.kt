package com.example.penasim.ui.command

import com.example.penasim.domain.*
import com.example.penasim.domain.repository.*
import com.example.penasim.testing.MainDispatcherRule
import com.example.penasim.usecase.FielderAppointmentUseCase
import com.example.penasim.usecase.MainMembersUseCase
import com.example.penasim.usecase.PitcherAppointmentUseCase
import com.example.penasim.usecase.PlayerInfoUseCase
import com.example.penasim.usecase.TeamUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CommandViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun mkPlayer(id: Int, teamId: Int, name: String): Player = Player(
        id = id,
        firstName = name,
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
    fun setTeamId_populatesState_fromUseCases() = runTest {
        val team = Team(0, "T", League.L1)

        val teamRepo = object : TeamRepository {
            override suspend fun getTeam(id: Int): Team? = if (id == team.id) team else null
            override suspend fun getTeamsByLeague(league: League): List<Team> = listOf(team)
            override suspend fun getAllTeams(): List<Team> = listOf(team)
        }
        val playerRepo = object : PlayerRepository {
            private val players = listOf(mkPlayer(1, team.id, "P1"), mkPlayer(2, team.id, "P2"))
            override suspend fun getPlayerCount(teamId: Int): Int = players.size
            override suspend fun getPlayers(teamId: Int): List<Player> = players.filter { it.teamId == teamId }
            override suspend fun getPlayer(id: Int): Player? = players.find { it.id == id }
            override suspend fun getAllPlayers(): List<Player> = players
        }
        val positionRepo = object : PlayerPositionRepository {
            override suspend fun getPlayerPositions(playerId: Int): List<PlayerPosition> = listOf(
                PlayerPosition(playerId, Position.OUTFIELDER, 10)
            )
            override suspend fun getAllPlayerPositions(): List<PlayerPosition> = emptyList()
            override suspend fun getAllPlayerPositionsByPosition(position: Position): List<PlayerPosition> = emptyList()
        }
        val battingRepo = object : BattingStatRepository {
            override suspend fun getByFixtureId(fixtureId: Int) = emptyList<BattingStat>()
            override suspend fun getByFixtureIds(fixtureIds: List<Int>) = emptyList<BattingStat>()
            override suspend fun getByPlayerId(playerId: Int) = emptyList<BattingStat>()
            override suspend fun getByPlayerIds(playerIds: List<Int>) = emptyList<BattingStat>()
            override suspend fun insertAll(items: List<BattingStat>) {}
            override suspend fun deleteByFixtureId(fixtureId: Int) {}
        }
        val pitchingRepo = object : PitchingStatRepository {
            override suspend fun getByFixtureId(fixtureId: Int) = emptyList<PitchingStat>()
            override suspend fun getByFixtureIds(fixtureIds: List<Int>) = emptyList<PitchingStat>()
            override suspend fun getByPlayerId(playerId: Int) = emptyList<PitchingStat>()
            override suspend fun getByPlayerIds(playerIds: List<Int>) = emptyList<PitchingStat>()
            override suspend fun insertAll(items: List<PitchingStat>) {}
            override suspend fun deleteByFixtureId(fixtureId: Int) {}
        }
        val fielderRepo = object : FielderAppointmentRepository {
            private val data = listOf(
                FielderAppointment(team.id, 1, Position.OUTFIELDER, 1, OrderType.NORMAL),
                FielderAppointment(team.id, 2, Position.BENCH, 0, OrderType.NORMAL),
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
        val pitcherRepo = object : PitcherAppointmentRepository {
            private val data = listOf(
                com.example.penasim.domain.PitcherAppointment(team.id, 1, PitcherType.STARTER, 1),
                com.example.penasim.domain.PitcherAppointment(team.id, 2, PitcherType.RELIEVER, 1),
            )
            override suspend fun getPitcherAppointmentsByTeamId(teamId: Int) = data.filter { it.teamId == teamId }
            override suspend fun getPitcherAppointmentByPlayerId(playerId: Int) = data.find { it.playerId == playerId }
            override suspend fun insertPitcherAppointment(pitcherAppointment: com.example.penasim.domain.PitcherAppointment) {}
            override suspend fun insertPitcherAppointments(pitcherAppointments: List<com.example.penasim.domain.PitcherAppointment>) {}
            override suspend fun deletePitcherAppointment(pitcherAppointment: com.example.penasim.domain.PitcherAppointment) {}
            override suspend fun deletePitcherAppointments(pitcherAppointments: List<com.example.penasim.domain.PitcherAppointment>) {}
            override suspend fun updatePitcherAppointment(pitcherAppointment: com.example.penasim.domain.PitcherAppointment) {}
            override suspend fun updatePitcherAppointments(pitcherAppointments: List<com.example.penasim.domain.PitcherAppointment>) {}
        }
        val mainRepo = object : MainMembersRepository {
            private val data = listOf(
                MainMember(team.id, 1, MemberType.MAIN, isFielder = true),
                MainMember(team.id, 2, MemberType.SUB, isFielder = true),
            )
            override suspend fun getMainMembersByTeamId(teamId: Int) = data.filter { it.teamId == teamId }
            override suspend fun getMainMemberByPlayerId(playerId: Int) = data.find { it.playerId == playerId }
            override suspend fun insertMainMember(mainMember: MainMember) {}
            override suspend fun insertMainMembers(mainMembers: List<MainMember>) {}
            override suspend fun deleteMainMember(mainMember: MainMember) {}
            override suspend fun deleteMainMembers(mainMembers: List<MainMember>) {}
            override suspend fun updateMainMember(mainMember: MainMember) {}
            override suspend fun updateMainMembers(mainMembers: List<MainMember>) {}
        }

        val teamUseCase = TeamUseCase(teamRepo, playerRepo, fielderRepo, pitcherRepo, positionRepo, battingRepo, pitchingRepo)
        val playerInfoUseCase = PlayerInfoUseCase(playerRepo, positionRepo, teamRepo, battingRepo, pitchingRepo)
        val fielderUseCase = FielderAppointmentUseCase(fielderRepo)
        val pitcherUseCase = PitcherAppointmentUseCase(pitcherRepo)
        val mainUseCase = MainMembersUseCase(mainRepo)

        val vm = CommandViewModel(teamUseCase, playerInfoUseCase, fielderUseCase, pitcherUseCase, mainUseCase)

        vm.setTeamId(team.id)

        val state = vm.uiState.value
        assertEquals(team, state.team)
        assertEquals(2, state.players.size)
        assertEquals(2, state.fielderAppointments.size)
        assertEquals(2, state.pitcherAppointments.size)
        assertEquals(2, state.mainMembers.size)
        assertNull(state.mainViewSelectedFielderId)
        assertTrue(state.selectedFielder.values.all { it == null })
        assertNull(state.selectedPitcherId)
    }

    @Test
    fun update_and_select_operations_work_as_expected() = runTest {
        val team = Team(0, "T", League.L1)

        // Minimal repos reused from previous test
        val teamRepo = object : TeamRepository {
            override suspend fun getTeam(id: Int): Team? = team
            override suspend fun getTeamsByLeague(league: League): List<Team> = listOf(team)
            override suspend fun getAllTeams(): List<Team> = listOf(team)
        }
        val playerRepo = object : PlayerRepository {
            private val players = listOf(mkPlayer(1, team.id, "P1"), mkPlayer(2, team.id, "P2"))
            override suspend fun getPlayerCount(teamId: Int): Int = players.size
            override suspend fun getPlayers(teamId: Int): List<Player> = players
            override suspend fun getPlayer(id: Int): Player? = players.find { it.id == id }
            override suspend fun getAllPlayers(): List<Player> = players
        }
        val positionRepo = object : PlayerPositionRepository {
            override suspend fun getPlayerPositions(playerId: Int): List<PlayerPosition> = listOf(
                PlayerPosition(playerId, Position.OUTFIELDER, 10)
            )
            override suspend fun getAllPlayerPositions(): List<PlayerPosition> = emptyList()
            override suspend fun getAllPlayerPositionsByPosition(position: Position): List<PlayerPosition> = emptyList()
        }
        val battingRepo = object : BattingStatRepository {
            override suspend fun getByFixtureId(fixtureId: Int) = emptyList<BattingStat>()
            override suspend fun getByFixtureIds(fixtureIds: List<Int>) = emptyList<BattingStat>()
            override suspend fun getByPlayerId(playerId: Int) = emptyList<BattingStat>()
            override suspend fun getByPlayerIds(playerIds: List<Int>) = emptyList<BattingStat>()
            override suspend fun insertAll(items: List<BattingStat>) {}
            override suspend fun deleteByFixtureId(fixtureId: Int) {}
        }
        val pitchingRepo = object : PitchingStatRepository {
            override suspend fun getByFixtureId(fixtureId: Int) = emptyList<PitchingStat>()
            override suspend fun getByFixtureIds(fixtureIds: List<Int>) = emptyList<PitchingStat>()
            override suspend fun getByPlayerId(playerId: Int) = emptyList<PitchingStat>()
            override suspend fun getByPlayerIds(playerIds: List<Int>) = emptyList<PitchingStat>()
            override suspend fun insertAll(items: List<PitchingStat>) {}
            override suspend fun deleteByFixtureId(fixtureId: Int) {}
        }
        val initialFielders = mutableListOf(
            FielderAppointment(team.id, 1, Position.OUTFIELDER, 1, OrderType.NORMAL),
            FielderAppointment(team.id, 2, Position.BENCH, 0, OrderType.NORMAL),
        )
        val fielderRepo = object : FielderAppointmentRepository {
            override suspend fun getFielderAppointmentByPlayerId(playerId: Int) = initialFielders.find { it.playerId == playerId }
            override suspend fun getFielderAppointmentsByTeamId(teamId: Int) = initialFielders.toList()
            override suspend fun insertFielderAppointment(fielderAppointment: FielderAppointment) {}
            override suspend fun insertFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
            override suspend fun deleteFielderAppointment(fielderAppointment: FielderAppointment) {}
            override suspend fun deleteFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
            override suspend fun updateFielderAppointment(fielderAppointment: FielderAppointment) {}
            override suspend fun updateFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
        }
        val initialPitchers = mutableListOf(
            com.example.penasim.domain.PitcherAppointment(team.id, 1, PitcherType.STARTER, 1),
            com.example.penasim.domain.PitcherAppointment(team.id, 2, PitcherType.RELIEVER, 2),
        )
        val pitcherRepo = object : PitcherAppointmentRepository {
            override suspend fun getPitcherAppointmentsByTeamId(teamId: Int) = initialPitchers.toList()
            override suspend fun getPitcherAppointmentByPlayerId(playerId: Int) = initialPitchers.find { it.playerId == playerId }
            override suspend fun insertPitcherAppointment(pitcherAppointment: com.example.penasim.domain.PitcherAppointment) {}
            override suspend fun insertPitcherAppointments(pitcherAppointments: List<com.example.penasim.domain.PitcherAppointment>) {}
            override suspend fun deletePitcherAppointment(pitcherAppointment: com.example.penasim.domain.PitcherAppointment) {}
            override suspend fun deletePitcherAppointments(pitcherAppointments: List<com.example.penasim.domain.PitcherAppointment>) {}
            override suspend fun updatePitcherAppointment(pitcherAppointment: com.example.penasim.domain.PitcherAppointment) {}
            override suspend fun updatePitcherAppointments(pitcherAppointments: List<com.example.penasim.domain.PitcherAppointment>) {}
        }
        val mainMembers = mutableListOf(
            MainMember(team.id, 1, MemberType.MAIN, isFielder = true),
            MainMember(team.id, 2, MemberType.SUB, isFielder = true),
        )
        val mainRepo = object : MainMembersRepository {
            override suspend fun getMainMembersByTeamId(teamId: Int) = mainMembers.toList()
            override suspend fun getMainMemberByPlayerId(playerId: Int) = mainMembers.find { it.playerId == playerId }
            override suspend fun insertMainMember(mainMember: MainMember) {}
            override suspend fun insertMainMembers(mainMembers: List<MainMember>) {}
            override suspend fun deleteMainMember(mainMember: MainMember) {}
            override suspend fun deleteMainMembers(mainMembers: List<MainMember>) {}
            override suspend fun updateMainMember(mainMember: MainMember) {}
            override suspend fun updateMainMembers(mainMembers: List<MainMember>) {}
        }

        val teamUseCase = TeamUseCase(teamRepo, playerRepo, fielderRepo, pitcherRepo, positionRepo, battingRepo, pitchingRepo)
        val playerInfoUseCase = PlayerInfoUseCase(playerRepo, positionRepo, teamRepo, battingRepo, pitchingRepo)
        val fielderUseCase = FielderAppointmentUseCase(fielderRepo)
        val pitcherUseCase = PitcherAppointmentUseCase(pitcherRepo)
        val mainUseCase = MainMembersUseCase(mainRepo)

        val vm = CommandViewModel(teamUseCase, playerInfoUseCase, fielderUseCase, pitcherUseCase, mainUseCase)
        vm.setTeamId(team.id)

        // updateMainFielder: change player 2 from SUB to MAIN
        vm.updateMainFielder(2, MemberType.MAIN)
        val updatedMember = vm.uiState.value.mainMembers.find { it.playerId == 2 }
        assertEquals(MemberType.MAIN, updatedMember?.memberType)

        // updateFielderAppointment: set player 2 from BENCH to OUTFIELDER number 3
        vm.updateFielderAppointment(2, Position.OUTFIELDER, 3, OrderType.NORMAL)
        val updatedApp = vm.uiState.value.fielderAppointments.find { it.playerId == 2 && it.orderType == OrderType.NORMAL }
        assertEquals(Position.OUTFIELDER, updatedApp?.position)
        assertEquals(3, updatedApp?.number)

        // selectFielder: select player1 then player2 -> since one is BENCH, swap position & number
        vm.selectFielder(1, OrderType.NORMAL) // select currentSelected = 1
        vm.selectFielder(2, OrderType.NORMAL) // swap
        val app1 = vm.uiState.value.fielderAppointments.find { it.playerId == 1 && it.orderType == OrderType.NORMAL }
        val app2 = vm.uiState.value.fielderAppointments.find { it.playerId == 2 && it.orderType == OrderType.NORMAL }
        // After swap, player1 should take player's2 previous pos/num (OUTFIELDER,3) and player2 takes player's1 previous pos/num
        assertEquals(Position.OUTFIELDER, app1?.position)
        assertEquals(3, app1?.number)
        assertEquals(Position.OUTFIELDER, app2?.position)
        assertEquals(1, app2?.number)

        // selectPitcher toggle and swap numbers/types
        vm.selectPitcher(1)
        vm.selectPitcher(2)
        val p1 = vm.uiState.value.pitcherAppointments.find { it.playerId == 1 }
        val p2 = vm.uiState.value.pitcherAppointments.find { it.playerId == 2 }
        assertEquals(PitcherType.RELIEVER, p1?.type)
        assertEquals(2, p1?.number)
        assertEquals(PitcherType.STARTER, p2?.type)
        assertEquals(1, p2?.number)
    }
}

