package com.example.penasim.data.mapper

import com.example.penasim.data.entity.PitcherAppointmentEntity
import com.example.penasim.domain.PitcherAppointment

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
