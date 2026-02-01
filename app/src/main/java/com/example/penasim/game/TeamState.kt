package com.example.penasim.game

import com.example.penasim.domain.PitcherType
import com.example.penasim.domain.TeamPlayers
import com.example.penasim.domain.isStarting

data class PitcherState(
  val playerId: Int,
  val stamina: Int
)

data class BatterState(
  val playerId: Int,
  val battingOrder: Int // 1〜9
)

data class TeamState(
  val players: TeamPlayers,
  var pitcher: PitcherState = PitcherState(
    playerId = 0,
    stamina = 0
  ),
  var batter: BatterState = BatterState(
    playerId = 0,
    battingOrder = 1
  )
) {

  init {
    val pitcherAppointment = players.pitcherAppointments.find { it.type == PitcherType.STARTER && it.number == 1 } ?: throw IllegalStateException("Starting pitcher not found")
    val pitcherInfo = players.players.find { it.player.id == pitcherAppointment.playerId } ?: throw IllegalStateException("Pitcher info not found for playerId ${pitcherAppointment.playerId}")
    pitcher = PitcherState(
      playerId = pitcherAppointment.playerId,
      stamina = pitcherInfo.player.stamina
    )

    val batterAppointment = players.fielderAppointments.find { it.position.isStarting() && it.number == 1 } ?: throw IllegalStateException("Starting batter not found")
    batter = BatterState(
      playerId = batterAppointment.playerId,
      battingOrder = 1
    )
  }

  fun goNextBatter() {
    val newBattingOrder = if (batter.battingOrder >= 9) 1 else batter.battingOrder + 1
    val newBatterId = players.fielderAppointments.filter { it.position.isStarting() }
      .find { it.number == newBattingOrder }?.playerId
      ?: throw IllegalStateException("Batter not found for batting order $newBattingOrder")

    batter = BatterState(
      playerId = newBatterId,
      battingOrder = newBattingOrder
    )
  }

  fun decreasePitcherStamina() {
    val rate = (2..3).random()

    pitcher = pitcher.copy(
      stamina = pitcher.stamina - rate
    )
  }
}

