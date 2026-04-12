package com.example.penasim.data.repository

import com.example.penasim.data.dao.FielderAppointmentDao
import com.example.penasim.data.mapper.toDomain
import com.example.penasim.data.mapper.toEntity
import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.repository.FielderAppointmentRepository
import javax.inject.Inject

class FielderAppointmentRepository @Inject constructor(
  private val dao: FielderAppointmentDao
) : FielderAppointmentRepository {
  override suspend fun getFielderAppointmentsByTeamId(teamId: Int): List<FielderAppointment> =
    dao.getByTeamId(teamId).map { it.toDomain() }

  override suspend fun getFielderAppointmentByPlayerId(playerId: Int): FielderAppointment? =
    dao.getByPlayerId(playerId)?.toDomain()

  override suspend fun insertFielderAppointment(fielderAppointment: FielderAppointment) {
    dao.insert(fielderAppointment.toEntity())
  }

  override suspend fun insertFielderAppointments(fielderAppointments: List<FielderAppointment>) {
    dao.insertAll(fielderAppointments.map { it.toEntity() })
  }

  override suspend fun deleteFielderAppointment(fielderAppointment: FielderAppointment) {
    dao.delete(fielderAppointment.toEntity())
  }

  override suspend fun deleteFielderAppointments(fielderAppointments: List<FielderAppointment>) {
    dao.deleteAll(fielderAppointments.map { it.toEntity() })
  }

  override suspend fun updateFielderAppointment(fielderAppointment: FielderAppointment) {
    dao.update(fielderAppointment.toEntity())
  }

  override suspend fun updateFielderAppointments(fielderAppointments: List<FielderAppointment>) {
    dao.updateAll(fielderAppointments.map { it.toEntity() })
  }
}
