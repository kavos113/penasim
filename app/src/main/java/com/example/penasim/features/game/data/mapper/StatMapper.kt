package com.example.penasim.features.game.data.mapper

import com.example.penasim.features.game.data.entity.StatEntity
import com.example.penasim.features.game.domain.Stat

fun StatEntity.toDomain(): Stat = Stat(
  id = id,
  gameFixtureId = gameFixtureId,
  batterId = batterId,
  pitcherId = pitcherId,
  inning = inning,
  outCount = outCount,
  hitCount = hitCount,
  earnedRun = earnedRun,
  result = result,
)

fun Stat.toEntity(): StatEntity = StatEntity(
  id = id,
  gameFixtureId = gameFixtureId,
  batterId = batterId,
  pitcherId = pitcherId,
  inning = inning,
  outCount = outCount,
  hitCount = hitCount,
  earnedRun = earnedRun,
  result = result,
)
