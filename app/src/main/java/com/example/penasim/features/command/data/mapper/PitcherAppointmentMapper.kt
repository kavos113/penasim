package com.example.penasim.features.command.data.mapper

import com.example.penasim.features.command.data.entity.PitcherAppointmentEntity
import com.example.penasim.features.command.domain.PitcherAppointment

fun PitcherAppointmentEntity.toDomain(): PitcherAppointment = PitcherAppointment(
  teamId = teamId,
  playerId = playerId,
  type = type,
  number = number,
)

fun PitcherAppointment.toEntity(): PitcherAppointmentEntity = PitcherAppointmentEntity(
  teamId = teamId,
  playerId = playerId,
  type = type,
  number = number,
)
