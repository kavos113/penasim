package com.example.penasim.data.mapper

import com.example.penasim.data.entity.GameFixtureEntity
import com.example.penasim.domain.GameFixture

fun GameFixtureEntity.toDomain(): GameFixture = GameFixture(
  id = id,
  date = date,
  numberOfGames = numberOfGames,
  homeTeamId = homeTeamId,
  awayTeamId = awayTeamId
)

fun GameFixture.toEntity(): GameFixtureEntity = GameFixtureEntity(
  id = id,
  date = date,
  numberOfGames = numberOfGames,
  homeTeamId = homeTeamId,
  awayTeamId = awayTeamId
)