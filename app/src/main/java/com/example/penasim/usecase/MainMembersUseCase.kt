package com.example.penasim.usecase

import com.example.penasim.domain.MainMember
import com.example.penasim.domain.repository.MainMembersRepository
import javax.inject.Inject

class MainMembersUseCase @Inject constructor(
    private val repository: MainMembersRepository
) {
    suspend fun getByTeamId(teamId: Int): List<MainMember> = repository.getMainMembersByTeamId(teamId)
    suspend fun getByPlayerId(playerId: Int): MainMember? = repository.getMainMemberByPlayerId(playerId)

    suspend fun insertOne(item: MainMember) = repository.insertMainMember(item)
    suspend fun insertMany(items: List<MainMember>) {
        if (items.isEmpty()) return
        repository.insertMainMembers(items)
    }

    suspend fun deleteOne(item: MainMember) = repository.deleteMainMember(item)
    suspend fun deleteMany(items: List<MainMember>) {
        if (items.isEmpty()) return
        repository.deleteMainMembers(items)
    }

    suspend fun updateOne(item: MainMember) = repository.updateMainMember(item)
    suspend fun updateMany(items: List<MainMember>) {
        if (items.isEmpty()) return
        repository.updateMainMembers(items)
    }

    suspend fun updateTeamMembers(members: List<MainMember>) {
        require(members.isNotEmpty()) { "No members provided" }
        require(members.map { it.teamId }.distinct().size == 1) { "Members must belong to the same team" }
        require(members.map { it.playerId }.distinct().size == members.size) { "Duplicate player ID in members" }

        val teamId = members.first().teamId
        val currentMembers = repository
            .getMainMembersByTeamId(teamId)
            .associateBy { it.playerId }

        val toUpdate = members.mapNotNull { newMember ->
            val currentMember = currentMembers[newMember.playerId]
            if (currentMember == null || currentMember != newMember) newMember else null
        }
        if (toUpdate.isEmpty()) return
        repository.updateMainMembers(toUpdate)
    }
}
