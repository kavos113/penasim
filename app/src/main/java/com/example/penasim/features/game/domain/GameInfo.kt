package com.example.penasim.features.game.domain

data class GameInfo(
  val fixture: GameFixture,
  val homeTeam: Team,
  val awayTeam: Team,
  val result: GameResult,
)
