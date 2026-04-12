package com.example.penasim.features.command.ui.command

import com.example.penasim.core.session.InMemorySelectedTeamStore
import com.example.penasim.features.command.domain.FielderAppointment
import com.example.penasim.features.command.domain.MainMember
import com.example.penasim.features.command.domain.MemberType
import com.example.penasim.features.command.domain.OrderType
import com.example.penasim.features.command.domain.PitcherAppointment
import com.example.penasim.features.command.domain.PitcherType
import com.example.penasim.features.command.ui.command.CommandViewModel
import com.example.penasim.features.player.domain.Player
import com.example.penasim.features.player.domain.PlayerInfo
import com.example.penasim.features.player.domain.PlayerPosition
import com.example.penasim.features.player.domain.Position
import com.example.penasim.features.player.domain.TotalBattingStats
import com.example.penasim.features.player.domain.TotalPitchingStats
import com.example.penasim.features.standing.domain.TeamStanding
import com.example.penasim.features.team.domain.League
import com.example.penasim.features.team.domain.Team
import com.example.penasim.core.testing.MainDispatcherRule
import com.example.penasim.features.command.usecase.FielderAppointmentUseCase
import com.example.penasim.features.command.usecase.MainMembersUseCase
import com.example.penasim.features.command.usecase.PitcherAppointmentUseCase
import com.example.penasim.features.player.usecase.PlayerInfoUseCase
import com.example.penasim.features.team.usecase.TeamUseCase
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class CommandViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val selectedTeamStore = InMemorySelectedTeamStore()
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

        val vm = CommandViewModel(
            selectedTeamStore,
            teamUseCase,
            playerInfoUseCase,
            fielderAppointmentUseCase,
            pitcherAppointmentUseCase,
            mainMembersUseCase
        )

        vm.setTeamId(team.id)
        advanceUntilIdle()

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

        val vm = CommandViewModel(
            selectedTeamStore,
            teamUseCase,
            playerInfoUseCase,
            fielderAppointmentUseCase,
            pitcherAppointmentUseCase,
            mainMembersUseCase
        )
        vm.setTeamId(team.id)
        advanceUntilIdle()

        vm.updateMainFielder(2, MemberType.MAIN)
        val updatedMember = vm.uiState.value.mainMembers.find { it.playerId == 2 }
        assertEquals(MemberType.MAIN, updatedMember?.memberType)

        vm.updateFielderAppointment(2, Position.OUTFIELDER, 3, OrderType.NORMAL)
        val updatedApp = vm.uiState.value.fielderAppointments.find { it.playerId == 2 && it.orderType == OrderType.NORMAL }
        assertEquals(Position.OUTFIELDER, updatedApp?.position)
        assertEquals(3, updatedApp?.number)

        vm.selectFielder(1, OrderType.NORMAL)
        vm.selectFielder(2, OrderType.NORMAL)
        val app1 = vm.uiState.value.fielderAppointments.find { it.playerId == 1 && it.orderType == OrderType.NORMAL }
        val app2 = vm.uiState.value.fielderAppointments.find { it.playerId == 2 && it.orderType == OrderType.NORMAL }
        assertEquals(Position.OUTFIELDER, app1?.position)
        assertEquals(3, app1?.number)
        assertEquals(Position.OUTFIELDER, app2?.position)
        assertEquals(1, app2?.number)

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


