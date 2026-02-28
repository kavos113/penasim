package com.example.penasim.domain

data class Stat(
  val id: Int = 0,
  val gameFixtureId: Int,
  val batterId: Int,
  val pitcherId: Int,
  val inning: Int,
  val outCount: Int,
  val hitCount: Int,
  val earnedRun: Int,
  val result: String,
)
