package com.example.penasim.data.mapper

import com.example.penasim.data.entity.PlayerEntity
import com.example.penasim.domain.Player

fun PlayerEntity.toDomain(): Player = Player(
    id = id,
    firstName = firstName,
    lastName = lastName,
    teamId = teamId,
    meet = meet,
    power = power,
    speed = speed,
    throwing = throwing,
    defense = defense,
    catching = catching,
    ballSpeed = ballSpeed,
    control = control,
    stamina = stamina,
    starter = starter,
    reliever = reliever,
)

fun Player.toEntity(): PlayerEntity = PlayerEntity(
    id = id,
    firstName = firstName,
    lastName = lastName,
    teamId = teamId,
    meet = meet,
    power = power,
    speed = speed,
    throwing = throwing,
    defense = defense,
    catching = catching,
    ballSpeed = ballSpeed,
    control = control,
    stamina = stamina,
    starter = starter,
    reliever = reliever,
)
