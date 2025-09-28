package com.example.penasim.usecase

import com.example.penasim.domain.MainMember
import com.example.penasim.domain.repository.MainMembersRepository
import javax.inject.Inject

class UpdateMainMembersUseCase @Inject constructor(
    private val mainMembersRepository: MainMembersRepository
) {
    suspend fun execute(members: List<MainMember>) {
        assert(members.isNotEmpty()) { "No members provided" }
        assert(members.map { it.teamId }.distinct().size == 1) { "Members must belong to the same team" }
        assert(members.map { it.playerId }.distinct().size == members.size) { "Duplicate player ID in members" }

        val teamId = members.firstOrNull()?.teamId
            ?: throw IllegalArgumentException("No members provided")

        val currentMembers = mainMembersRepository
            .getMainMembersByTeamId(teamId)
            .associateBy { it.playerId }

        // check changed members
        val toUpdate = members.mapNotNull { newMember ->
            val currentMember = currentMembers[newMember.playerId]
            if (currentMember == null || currentMember != newMember) {
                newMember
            } else { null }
        }

        mainMembersRepository.updateMainMembers(toUpdate)
    }
}