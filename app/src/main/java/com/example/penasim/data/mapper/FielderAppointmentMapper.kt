package com.example.penasim.data.mapper

import com.example.penasim.data.entity.FielderAppointmentEntity
import com.example.penasim.domain.FielderAppointment

fun FielderAppointmentEntity.toDomain(): FielderAppointment = FielderAppointment(
    teamId = teamId,
    playerId = playerId,
    position = position,
    isMain = isMain,
    number = number,
)

fun FielderAppointment.toEntity(): FielderAppointmentEntity = FielderAppointmentEntity(
    teamId = teamId,
    playerId = playerId,
    position = position,
    isMain = isMain,
    number = number,
)
