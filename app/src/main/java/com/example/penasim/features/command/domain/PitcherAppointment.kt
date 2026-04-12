package com.example.penasim.features.command.domain

data class PitcherAppointment(
  val teamId: Int,
  val playerId: Int,
  val type: PitcherType,
  val number: Int, // 登板順
)

enum class PitcherType {
  STARTER,
  RELIEVER,
  CLOSER,
  SUB
}