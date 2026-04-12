package com.example.penasim.features.command.data.repository

import com.example.penasim.features.command.data.dao.PitcherAppointmentDao
import com.example.penasim.features.command.data.mapper.toDomain
import com.example.penasim.features.command.data.mapper.toEntity
import com.example.penasim.features.command.domain.PitcherAppointment
import com.example.penasim.features.command.domain.repository.PitcherAppointmentRepository
import javax.inject.Inject

class PitcherAppointmentRepository @Inject constructor(
  private val dao: PitcherAppointmentDao
) : PitcherAppointmentRepository {
  override suspend fun getPitcherAppointmentsByTeamId(teamId: Int): List<PitcherAppointment> =
    dao.getByTeamId(teamId).map { it.toDomain() }

  override suspend fun getPitcherAppointmentByPlayerId(playerId: Int): PitcherAppointment? =
    dao.getByPlayerId(playerId)?.toDomain()

  override suspend fun insertPitcherAppointment(pitcherAppointment: PitcherAppointment) {
    dao.insert(pitcherAppointment.toEntity())
  }

  override suspend fun insertPitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {
    dao.insertAll(pitcherAppointments.map { it.toEntity() })
  }

  override suspend fun deletePitcherAppointment(pitcherAppointment: PitcherAppointment) {
    dao.delete(pitcherAppointment.toEntity())
  }

  override suspend fun deletePitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {
    dao.deleteAll(pitcherAppointments.map { it.toEntity() })
  }

  override suspend fun updatePitcherAppointment(pitcherAppointment: PitcherAppointment) {
    dao.update(pitcherAppointment.toEntity())
  }

  override suspend fun updatePitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {
    dao.updateAll(pitcherAppointments.map { it.toEntity() })
  }
}
