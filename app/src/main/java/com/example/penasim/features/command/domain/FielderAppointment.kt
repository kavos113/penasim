package com.example.penasim.features.command.domain

data class FielderAppointment(
  val teamId: Int,
  val playerId: Int,
  val position: Position,
  val number: Int, // 打順, 10以降は控え
  val orderType: OrderType,
)

enum class OrderType {
  NORMAL,
  LEFT,
  DH,
  LEFT_DH,
}