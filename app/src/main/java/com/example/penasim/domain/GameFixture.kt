package com.example.penasim.domain

import java.time.LocalDate

data class GameFixture(
  val id: Int,
  val date: LocalDate,
  val numberOfGames: Int, // 節内で何番目か
  val homeTeamId: Int,
  val awayTeamId: Int,
)