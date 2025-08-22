package com.example.penasim.data.mapper

import com.example.penasim.data.entity.GameEntity
import com.example.penasim.domain.Game
import com.example.penasim.domain.GameMaster

fun GameEntity.toDomain(master: GameMaster) = Game(
    id = id,
    master = master,
    homeScore = homeScore,
    awayScore = awayScore,
)

fun Game.toEntity(): GameEntity = GameEntity(
    id = id,
    gameMasterId = master.id,
    homeScore = homeScore,
    awayScore = awayScore
)