package com.example.penasim.domain

data class InningScore(
  val fixtureId: Int,
  val teamId: Int,
  val inning: Int,
  val score: Int
)