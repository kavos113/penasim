package com.example.penasim.data.mapper

import com.example.penasim.data.entity.HomeRunEntity
import com.example.penasim.domain.HomeRun

fun HomeRunEntity.toDomain(): HomeRun {
  return HomeRun(
    fixtureId = fixtureId,
    playerId = playerId,
    inning = inning,
    count = count,
  )
}

fun HomeRun.toEntity(): HomeRunEntity {
  return HomeRunEntity(
    fixtureId = fixtureId,
    playerId = playerId,
    inning = inning,
    count = count,
  )
}
