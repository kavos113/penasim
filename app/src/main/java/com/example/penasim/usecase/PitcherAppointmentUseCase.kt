package com.example.penasim.usecase

import com.example.penasim.domain.PitcherAppointment
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.PitcherAppointmentRepository
import javax.inject.Inject

class PitcherAppointmentUseCase @Inject constructor(
    private val repository: PitcherAppointmentRepository
) {
    suspend fun getByTeam(team: Team): List<PitcherAppointment> =
        repository.getPitcherAppointmentsByTeamId(team.id)

    suspend fun getByPlayerId(playerId: Int): PitcherAppointment? =
        repository.getPitcherAppointmentByPlayerId(playerId)

    suspend fun insertOne(item: PitcherAppointment) = repository.insertPitcherAppointment(item)
    suspend fun insertMany(items: List<PitcherAppointment>) {
        if (items.isEmpty()) return
        repository.insertPitcherAppointments(items)
    }

    suspend fun deleteOne(item: PitcherAppointment) = repository.deletePitcherAppointment(item)
    suspend fun deleteMany(items: List<PitcherAppointment>) {
        if (items.isEmpty()) return
        repository.deletePitcherAppointments(items)
    }

    suspend fun updateOne(item: PitcherAppointment) = repository.updatePitcherAppointment(item)
    suspend fun updateMany(items: List<PitcherAppointment>) {
        if (items.isEmpty()) return
        repository.updatePitcherAppointments(items)
    }

    suspend fun updateTeamAppointments(appointments: List<PitcherAppointment>) {
        require(appointments.isNotEmpty()) { "No appointments provided" }
        require(appointments.map { it.teamId }.distinct().size == 1) { "Appointments must belong to the same team" }
        require(appointments.map { it.playerId }.distinct().size == appointments.size) { "Duplicate player IDs in appointments" }

        val teamId = appointments.first().teamId
        val currentAppointments = repository
            .getPitcherAppointmentsByTeamId(teamId)
            .associateBy { it.playerId }

        val toUpdate = appointments.mapNotNull { newApp ->
            val currentApp = currentAppointments[newApp.playerId]
            if (currentApp == null || currentApp != newApp) newApp else null
        }
        if (toUpdate.isEmpty()) return
        repository.updatePitcherAppointments(toUpdate)
    }
}
