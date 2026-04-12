package com.example.penasim.features.game.domain

data class BattingStat(
  val gameFixtureId: Int,
  val playerId: Int,
  val atBat: Int = 0,
  val hit: Int = 0,
  val doubleHit: Int = 0,
  val tripleHit: Int = 0,
  val homeRun: Int = 0,
  val walk: Int = 0,
  val rbi: Int = 0,
  val strikeOut: Int = 0,
)
