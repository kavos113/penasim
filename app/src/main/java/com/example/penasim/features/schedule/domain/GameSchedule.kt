package com.example.penasim.features.schedule.domain

data class GameSchedule(
  val fixture: GameFixture,
  val homeTeam: Team,
  val awayTeam: Team,
)
