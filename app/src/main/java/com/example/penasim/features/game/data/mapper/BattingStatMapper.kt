package com.example.penasim.features.game.data.mapper

import com.example.penasim.features.game.data.entity.BattingStatEntity
import com.example.penasim.features.game.domain.BattingStat

fun BattingStatEntity.toDomain(): BattingStat = BattingStat(
  gameFixtureId = gameFixtureId,
  playerId = playerId,
  atBat = atBat,
  hit = hit,
  doubleHit = doubleHit,
  tripleHit = tripleHit,
  homeRun = homeRun,
  walk = walk,
  rbi = rbi,
  strikeOut = strikeOut,
)

fun BattingStat.toEntity(): BattingStatEntity = BattingStatEntity(
  gameFixtureId = gameFixtureId,
  playerId = playerId,
  atBat = atBat,
  hit = hit,
  doubleHit = doubleHit,
  tripleHit = tripleHit,
  homeRun = homeRun,
  walk = walk,
  rbi = rbi,
  strikeOut = strikeOut,
)
