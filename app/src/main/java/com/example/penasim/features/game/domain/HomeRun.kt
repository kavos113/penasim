package com.example.penasim.features.game.domain

data class HomeRun(
  val fixtureId: Int,
  val playerId: Int,
  val inning: Int,
  val count: Int,
)
