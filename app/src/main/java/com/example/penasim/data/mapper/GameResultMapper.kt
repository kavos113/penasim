package com.example.penasim.data.mapper

import com.example.penasim.data.entity.GameResultEntity
import com.example.penasim.domain.GameResult

fun GameResultEntity.toDomain() = GameResult(
    fixtureId = gameFixtureId,
    homeScore = homeScore,
    awayScore = awayScore,
)

fun GameResult.toEntity(): GameResultEntity = GameResultEntity(
    gameFixtureId = fixtureId,
    homeScore = homeScore,
    awayScore = awayScore,
)