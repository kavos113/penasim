package com.example.penasim.features.game.data.mapper

import com.example.penasim.features.game.data.entity.InningScoreEntity
import com.example.penasim.features.game.domain.InningScore

fun InningScoreEntity.toDomain(): InningScore = InningScore(
  fixtureId = gameFixtureId,
  teamId = teamId,
  inning = inning,
  score = score,
)

fun InningScore.toEntity(): InningScoreEntity = InningScoreEntity(
  gameFixtureId = fixtureId,
  teamId = teamId,
  inning = inning,
  score = score,
)
