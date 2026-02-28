package com.example.penasim.ui.command

import com.example.penasim.domain.*
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
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class CommandViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val teamUseCase: TeamUseCase = mock()
    private val playerInfoUseCase: PlayerInfoUseCase = mock()
    private val fielderAppointmentUseCase: FielderAppointmentUseCase = mock()
    private val pitcherAppointmentUseCase: PitcherAppointmentUseCase = mock()
    private val mainMembersUseCase: MainMembersUseCase = mock()

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
        val player1 = mkPlayer(1, team.id, "P1")
        val player2 = mkPlayer(2, team.id, "P2")
        val playerInfos = listOf(
            PlayerInfo(player1, listOf(PlayerPosition(1, Position.OUTFIELDER, 10)), team, TotalBattingStats(1), TotalPitchingStats(1)),
            PlayerInfo(player2, listOf(PlayerPosition(2, Position.OUTFIELDER, 10)), team, TotalBattingStats(2), TotalPitchingStats(2)),
        )
        val fielderAppointments = listOf(
            FielderAppointment(team.id, 1, Position.OUTFIELDER, 1, OrderType.NORMAL),
            FielderAppointment(team.id, 2, Position.BENCH, 0, OrderType.NORMAL),
        )
        val pitcherAppointments = listOf(
            PitcherAppointment(team.id, 1, PitcherType.STARTER, 1),
            PitcherAppointment(team.id, 2, PitcherType.RELIEVER, 1),
        )
        val mainMembers = listOf(
            MainMember(team.id, 1, MemberType.MAIN, isFielder = true),
            MainMember(team.id, 2, MemberType.SUB, isFielder = true),
        )

        whenever(teamUseCase.getTeam(team.id)).thenReturn(team)
        whenever(playerInfoUseCase.getByTeamId(team.id)).thenReturn(playerInfos)
        whenever(fielderAppointmentUseCase.getByTeam(any())).thenReturn(fielderAppointments)
        whenever(pitcherAppointmentUseCase.getByTeam(any())).thenReturn(pitcherAppointments)
        whenever(mainMembersUseCase.getByTeamId(team.id)).thenReturn(mainMembers)

        val vm = CommandViewModel(teamUseCase, playerInfoUseCase, fielderAppointmentUseCase, pitcherAppointmentUseCase, mainMembersUseCase)

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
        val player1 = mkPlayer(1, team.id, "P1")
        val player2 = mkPlayer(2, team.id, "P2")
        val playerInfos = listOf(
            PlayerInfo(player1, listOf(PlayerPosition(1, Position.OUTFIELDER, 10)), team, TotalBattingStats(1), TotalPitchingStats(1)),
            PlayerInfo(player2, listOf(PlayerPosition(2, Position.OUTFIELDER, 10)), team, TotalBattingStats(2), TotalPitchingStats(2)),
        )
        val fielderAppointments = listOf(
            FielderAppointment(team.id, 1, Position.OUTFIELDER, 1, OrderType.NORMAL),
            FielderAppointment(team.id, 2, Position.BENCH, 0, OrderType.NORMAL),
        )
        val pitcherAppointments = listOf(
            PitcherAppointment(team.id, 1, PitcherType.STARTER, 1),
            PitcherAppointment(team.id, 2, PitcherType.RELIEVER, 2),
        )
        val mainMembers = listOf(
            MainMember(team.id, 1, MemberType.MAIN, isFielder = true),
            MainMember(team.id, 2, MemberType.SUB, isFielder = true),
        )

        whenever(teamUseCase.getTeam(team.id)).thenReturn(team)
        whenever(playerInfoUseCase.getByTeamId(team.id)).thenReturn(playerInfos)
        whenever(fielderAppointmentUseCase.getByTeam(any())).thenReturn(fielderAppointments)
        whenever(pitcherAppointmentUseCase.getByTeam(any())).thenReturn(pitcherAppointments)
        whenever(mainMembersUseCase.getByTeamId(team.id)).thenReturn(mainMembers)

        val vm = CommandViewModel(teamUseCase, playerInfoUseCase, fielderAppointmentUseCase, pitcherAppointmentUseCase, mainMembersUseCase)
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

        // selectFielder: select player1 then player2 -> since one was BENCH, swap position & number
        vm.selectFielder(1, OrderType.NORMAL) // select currentSelected = 1
        vm.selectFielder(2, OrderType.NORMAL) // swap
        val app1 = vm.uiState.value.fielderAppointments.find { it.playerId == 1 && it.orderType == OrderType.NORMAL }
        val app2 = vm.uiState.value.fielderAppointments.find { it.playerId == 2 && it.orderType == OrderType.NORMAL }
        // After swap, player1 should take player2's previous pos/num (OUTFIELDER,3) and player2 takes player1's previous
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

