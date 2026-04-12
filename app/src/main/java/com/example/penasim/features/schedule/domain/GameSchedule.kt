package com.example.penasim.features.schedule.domain

import com.example.penasim.features.team.domain.Team

data class GameSchedule(
  val fixture: GameFixture,
  val homeTeam: Team,
  val awayTeam: Team,
)
