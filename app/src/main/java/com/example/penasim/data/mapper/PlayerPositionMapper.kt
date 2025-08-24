package com.example.penasim.data.mapper

import com.example.penasim.data.entity.PlayerPositionEntity
import com.example.penasim.domain.PlayerPosition

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
