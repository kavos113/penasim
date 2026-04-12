package com.example.penasim.features.command.domain

data class MainMember(
  val teamId: Int,
  val playerId: Int,
  val memberType: MemberType,
  val isFielder: Boolean,
)

enum class MemberType {
  MAIN,
  SUB,
}
