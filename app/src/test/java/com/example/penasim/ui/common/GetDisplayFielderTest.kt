package com.example.penasim.ui.common

import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.OrderType
import com.example.penasim.domain.PitcherAppointment
import com.example.penasim.domain.PitcherType
import com.example.penasim.domain.Player
import com.example.penasim.domain.PlayerInfo
import com.example.penasim.domain.PlayerPosition
import com.example.penasim.domain.Position
import com.example.penasim.domain.Team
import com.example.penasim.usecase.FielderAppointmentUseCase
import com.example.penasim.usecase.PitcherAppointmentUseCase
import com.example.penasim.usecase.PlayerInfoUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetDisplayFielderTest {

  private val playerInfoUseCase: PlayerInfoUseCase = mock()
  private val fielderAppointmentUseCase: FielderAppointmentUseCase = mock()
  private val pitcherAppointmentUseCase: PitcherAppointmentUseCase = mock()

  private val target = GetDisplayFielder(
    playerInfoUseCase = playerInfoUseCase,
    fielderAppointmentUseCase = fielderAppointmentUseCase,
    pitcherAppointmentUseCase = pitcherAppointmentUseCase,
  )

  private val team = Team(id = 1, name = "A")
  private val orderType = OrderType.NORMAL

  @Test
  fun getStartingMember_pitcherInFielder_isReplacedByStarterPitcher_keepsBattingOrderNumber() = runTest {
    val fielderPitcherId = 10
    val starterPitcherId = 11

    val appointments = listOf(
      FielderAppointment(teamId = team.id, playerId = 100, position = Position.CATCHER, number = 2, orderType = orderType),
      FielderAppointment(teamId = team.id, playerId = fielderPitcherId, position = Position.PITCHER, number = 1, orderType = orderType),
    )
    val pitchers = listOf(
      PitcherAppointment(teamId = team.id, playerId = starterPitcherId, type = PitcherType.STARTER, number = 1),
    )

    val players = listOf(
      playerInfo(id = 100, firstName = "Catcher"),
      playerInfo(id = fielderPitcherId, firstName = "FielderP"),
      playerInfo(id = starterPitcherId, firstName = "StarterP", primaryPosition = Position.PITCHER),
    )

    whenever(fielderAppointmentUseCase.getByTeam(team)).thenReturn(appointments)
    whenever(pitcherAppointmentUseCase.getByTeam(team)).thenReturn(pitchers)
    whenever(playerInfoUseCase.getByTeamId(team.id)).thenReturn(players)

    val actual = target.getStartingMember(team, orderType)

    assertEquals(listOf(100, starterPitcherId), actual.map { it.id })
    assertEquals(listOf(Position.CATCHER, Position.PITCHER), actual.map { it.position })
    assertEquals(listOf(2, 1), actual.map { it.number })
    assertEquals(listOf("Catcher", "StarterP"), actual.map { it.displayName })
  }

  @Test
  fun getStartingMember_whenNoPitcherSlot_returnsOnlyStartingFieldersSortedByNumber() = runTest {
    val appointments = listOf(
      FielderAppointment(teamId = team.id, playerId = 101, position = Position.CATCHER, number = 2, orderType = orderType),
      FielderAppointment(teamId = team.id, playerId = 100, position = Position.SHORTSTOP, number = 1, orderType = orderType),
      FielderAppointment(teamId = team.id, playerId = 999, position = Position.BENCH, number = 99, orderType = orderType), // filtered out
    )

    whenever(fielderAppointmentUseCase.getByTeam(team)).thenReturn(appointments)
    whenever(pitcherAppointmentUseCase.getByTeam(team)).thenReturn(emptyList())
    whenever(playerInfoUseCase.getByTeamId(team.id)).thenReturn(
      listOf(
        playerInfo(id = 100, firstName = "SS", primaryPosition = Position.SHORTSTOP),
        playerInfo(id = 101, firstName = "C", primaryPosition = Position.CATCHER),
      )
    )

    val actual = target.getStartingMember(team, orderType)

    assertEquals(listOf(100, 101), actual.map { it.id })
    assertEquals(listOf(1, 2), actual.map { it.number })
  }

  @Test
  fun getStartingMember_pitcherSlotButNoStarterPitcher_throws() = runTest {
    val appointments = listOf(
      FielderAppointment(teamId = team.id, playerId = 10, position = Position.PITCHER, number = 1, orderType = orderType),
    )

    whenever(fielderAppointmentUseCase.getByTeam(team)).thenReturn(appointments)
    whenever(pitcherAppointmentUseCase.getByTeam(team)).thenReturn(
      listOf(PitcherAppointment(teamId = team.id, playerId = 11, type = PitcherType.RELIEVER, number = 1))
    )
    whenever(playerInfoUseCase.getByTeamId(team.id)).thenReturn(
      listOf(playerInfo(id = 10, firstName = "FielderP"), playerInfo(id = 11, firstName = "Reliever", primaryPosition = Position.PITCHER))
    )

    assertFailsWith<IllegalStateException> {
      target.getStartingMember(team, orderType)
    }
  }

  @Test
  fun getMainMember_pitcherSlot_replacesWithStarter_andAddsOtherNonSubPitchersAsBench() = runTest {
    val appointments = listOf(
      FielderAppointment(teamId = team.id, playerId = 200, position = Position.PITCHER, number = 1, orderType = orderType),
      FielderAppointment(teamId = team.id, playerId = 201, position = Position.CATCHER, number = 2, orderType = orderType),
    )

    val starterId = 300
    val relieverId = 301
    val closerId = 302
    val subId = 303

    val pitchers = listOf(
      PitcherAppointment(teamId = team.id, playerId = starterId, type = PitcherType.STARTER, number = 1),
      PitcherAppointment(teamId = team.id, playerId = relieverId, type = PitcherType.RELIEVER, number = 2),
      PitcherAppointment(teamId = team.id, playerId = closerId, type = PitcherType.CLOSER, number = 3),
      PitcherAppointment(teamId = team.id, playerId = subId, type = PitcherType.SUB, number = 4),
    )

    whenever(fielderAppointmentUseCase.getByTeam(team)).thenReturn(appointments)
    whenever(pitcherAppointmentUseCase.getByTeam(team)).thenReturn(pitchers)
    whenever(playerInfoUseCase.getByTeamId(team.id)).thenReturn(
      listOf(
        playerInfo(id = 200, firstName = "FielderP"),
        playerInfo(id = 201, firstName = "Catcher", primaryPosition = Position.CATCHER),
        playerInfo(id = starterId, firstName = "Starter", primaryPosition = Position.PITCHER),
        playerInfo(id = relieverId, firstName = "Reliever", primaryPosition = Position.PITCHER),
        playerInfo(id = closerId, firstName = "Closer", primaryPosition = Position.PITCHER),
        playerInfo(id = subId, firstName = "Sub", primaryPosition = Position.PITCHER),
      )
    )

    val actual = target.getMainMember(team, orderType)

    // starting fielders (pitcher replaced) + bench pitchers (excluding SUB and excluding starter)
    assertEquals(
      listOf(201, starterId, relieverId, closerId),
      actual.map { it.id }
    )
    assertEquals(
      listOf(Position.CATCHER, Position.PITCHER, Position.BENCH, Position.BENCH),
      actual.map { it.position }
    )
    assertEquals(listOf(2, 1, 0, 0), actual.map { it.number })
  }

  @Test
  fun getMainMember_whenNoPitcherSlot_addsAllNonSubPitchersAsBench_includingStarter() = runTest {
    val appointments = listOf(
      FielderAppointment(teamId = team.id, playerId = 100, position = Position.CATCHER, number = 2, orderType = orderType),
    )

    val starterId = 300
    val relieverId = 301
    val subId = 302

    whenever(fielderAppointmentUseCase.getByTeam(team)).thenReturn(appointments)
    whenever(pitcherAppointmentUseCase.getByTeam(team)).thenReturn(
      listOf(
        PitcherAppointment(teamId = team.id, playerId = starterId, type = PitcherType.STARTER, number = 1),
        PitcherAppointment(teamId = team.id, playerId = relieverId, type = PitcherType.RELIEVER, number = 2),
        PitcherAppointment(teamId = team.id, playerId = subId, type = PitcherType.SUB, number = 3),
      )
    )
    whenever(playerInfoUseCase.getByTeamId(team.id)).thenReturn(
      listOf(
        playerInfo(id = 100, firstName = "C", primaryPosition = Position.CATCHER),
        playerInfo(id = starterId, firstName = "Starter", primaryPosition = Position.PITCHER),
        playerInfo(id = relieverId, firstName = "Reliever", primaryPosition = Position.PITCHER),
        playerInfo(id = subId, firstName = "Sub", primaryPosition = Position.PITCHER),
      )
    )

    val actual = target.getMainMember(team, orderType)

    assertEquals(
      listOf(100, starterId, relieverId),
      actual.map { it.id }
    )
    assertEquals(
      listOf(Position.CATCHER, Position.BENCH, Position.BENCH),
      actual.map { it.position }
    )
  }

  private fun playerInfo(
    id: Int,
    firstName: String,
    primaryPosition: Position = Position.OUTFIELDER,
  ): PlayerInfo {
    val player = Player(
      id = id,
      firstName = firstName,
      lastName = "",
      teamId = team.id,
      meet = 0,
      power = 0,
      speed = 0,
      throwing = 0,
      defense = 0,
      catching = 0,
      ballSpeed = 0,
      control = 0,
      stamina = 0,
      starter = 0,
      reliever = 0,
    )

    return PlayerInfo(
      player = player,
      positions = listOf(PlayerPosition(playerId = id, position = primaryPosition, defense = 50)),
      team = team,
      battingStat = dummyTotalBattingStats(id),
      pitchingStat = dummyTotalPitchingStats(id),
    )
  }

  // PlayerInfo は stats 必須なので、テスト用途のダミーを用意
  private fun dummyTotalBattingStats(playerId: Int) = com.example.penasim.domain.TotalBattingStats(
    playerId = playerId,
  )

  private fun dummyTotalPitchingStats(playerId: Int) = com.example.penasim.domain.TotalPitchingStats(
    playerId = playerId,
  )
}
