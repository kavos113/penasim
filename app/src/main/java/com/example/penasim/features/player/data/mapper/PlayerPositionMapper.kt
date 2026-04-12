package com.example.penasim.features.player.data.mapper

import com.example.penasim.features.player.data.entity.PlayerPositionEntity
import com.example.penasim.features.player.domain.PlayerPosition

fun PlayerPositionEntity.toDomain(): PlayerPosition = PlayerPosition(
  playerId = playerId,
  position = position,
  defense = defense,
)

fun PlayerPosition.toEntity(): PlayerPositionEntity = PlayerPositionEntity(
  playerId = playerId,
  position = position,
  defense = defense,
)
