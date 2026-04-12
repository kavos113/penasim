package com.example.penasim.features.game.domain

import com.example.penasim.features.schedule.domain.GameFixture
import com.example.penasim.features.team.domain.Team

data class GameInfo(
  val fixture: GameFixture,
  val homeTeam: Team,
  val awayTeam: Team,
  val result: GameResult,
)
