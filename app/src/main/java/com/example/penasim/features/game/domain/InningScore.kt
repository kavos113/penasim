package com.example.penasim.features.game.domain

data class InningScore(
  val fixtureId: Int,
  val teamId: Int,
  val inning: Int,
  val score: Int
)