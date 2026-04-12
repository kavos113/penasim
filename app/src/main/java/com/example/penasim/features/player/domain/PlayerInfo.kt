package com.example.penasim.domain

data class PlayerInfo(
  val player: Player,
  val positions: List<PlayerPosition>,
  val team: Team?,
  val battingStat: TotalBattingStats,
  val pitchingStat: TotalPitchingStats,
) {
  val fullName: String
    get() = "${player.firstName} ${player.lastName}"

  val isPitcher: Boolean
    get() = positions.any { it.position == Position.PITCHER }

  val primaryPosition: Position
    get() = positions.minByOrNull { it.defense }?.position
      ?: Position.OUTFIELDER // Default to OUTFIELDER if no positions are available (maybe impossible)
}
