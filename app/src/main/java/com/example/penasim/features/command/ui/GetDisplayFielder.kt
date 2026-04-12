package com.example.penasim.ui.common

import com.example.penasim.domain.OrderType
import com.example.penasim.domain.PitcherType
import com.example.penasim.domain.Position
import com.example.penasim.domain.Team
import com.example.penasim.domain.isStarting
import com.example.penasim.usecase.FielderAppointmentUseCase
import com.example.penasim.usecase.PitcherAppointmentUseCase
import com.example.penasim.usecase.PlayerInfoUseCase
import javax.inject.Inject

class GetDisplayFielder @Inject constructor(
  private val playerInfoUseCase: PlayerInfoUseCase,
  private val fielderAppointmentUseCase: FielderAppointmentUseCase,
  private val pitcherAppointmentUseCase: PitcherAppointmentUseCase,
) {
  // include pitcher
  suspend fun getStartingMember(team: Team, orderType: OrderType): List<DisplayFielder> {
    val fielderAppointment = fielderAppointmentUseCase.getByTeam(team)
    val pitcherAppointment = pitcherAppointmentUseCase.getByTeam(team)
    val players = playerInfoUseCase.getByTeamId(team.id)

    val fielders = fielderAppointment
      .filter { it.orderType == orderType && it.position.isStarting() }
      .map {
        DisplayFielder(
          id = it.playerId,
          displayName = players.find { player -> player.player.id == it.playerId }?.player?.firstName
            ?: "Unknown Player",
          position = it.position,
          number = it.number,
          color = players.find { player -> player.player.id == it.playerId }?.primaryPosition?.color()
            ?: Position.OUTFIELDER.color()
        )
      }
      .sortedBy { it.number }

    val pitcherFielder = fielders.find { it.position == Position.PITCHER }
    if (pitcherFielder != null) {
      val pitcher = pitcherAppointment.find { it.type == PitcherType.STARTER } ?: throw IllegalStateException("no starter pitchers")
      val pitcherPlayerInfo = players.find { it.player.id == pitcher.playerId }
      return fielders.filterNot { it.position == Position.PITCHER } + DisplayFielder(
        id = pitcher.playerId,
        displayName = pitcherPlayerInfo?.player?.firstName ?: "Unknown Player",
        position = Position.PITCHER,
        number = pitcherFielder.number,
        color = Position.PITCHER.color()
      )
    }
    return fielders
  }

  // starting + sub
  // include pitcher
  suspend fun getMainMember(team: Team, orderType: OrderType): List<DisplayFielder> {
    val players = playerInfoUseCase.getByTeamId(team.id)
    val fielderAppointment = fielderAppointmentUseCase.getByTeam(team)
    val pitcherAppointment = pitcherAppointmentUseCase.getByTeam(team)

    val fielders = fielderAppointment
      .filter { it.orderType == orderType && it.position.isStarting() }
      .map {
        DisplayFielder(
          id = it.playerId,
          displayName = players.find { player -> player.player.id == it.playerId }?.player?.firstName
            ?: "Unknown Player",
          position = it.position,
          number = it.number,
          color = players.find { player -> player.player.id == it.playerId }?.primaryPosition?.color()
            ?: Position.OUTFIELDER.color()
        )
      }
      .sortedBy { it.number }

    val pitcherFielder = fielders.find { it.position == Position.PITCHER }
    if (pitcherFielder != null) {
      val pitcher = pitcherAppointment.find { it.type == PitcherType.STARTER } ?: throw IllegalStateException("no starter pitchers")
      val pitcherPlayerInfo = players.find { it.player.id == pitcher.playerId }

      val otherPitchers = pitcherAppointment.filter { it.playerId != pitcher.playerId && it.type != PitcherType.SUB }

      return fielders.filterNot { it.position == Position.PITCHER } + DisplayFielder(
        id = pitcher.playerId,
        displayName = pitcherPlayerInfo?.player?.firstName ?: "Unknown Player",
        position = Position.PITCHER,
        number = pitcherFielder.number,
        color = Position.PITCHER.color()
      ) + otherPitchers.map { p ->
        val info = players.find { it.player.id == p.playerId }
        DisplayFielder(
          id = p.playerId,
          displayName = info?.player?.firstName ?: "Unknown Player",
          position = Position.BENCH,
          number = 0,
          color = Position.PITCHER.color()
        )
      }
    }

    val pitchers = pitcherAppointment.filter { it.type != PitcherType.SUB }
    return fielders + pitchers.map { p ->
      val info = players.find { it.player.id == p.playerId }
      DisplayFielder(
        id = p.playerId,
        displayName = info?.player?.firstName ?: "Unknown Player",
        position = Position.BENCH,
        number = 0,
        color = Position.PITCHER.color()
      )
    }
  }
}