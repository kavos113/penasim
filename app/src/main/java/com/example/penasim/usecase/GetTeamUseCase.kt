package com.example.penasim.usecase

import com.example.penasim.domain.repository.TeamRepository
import javax.inject.Inject

class GetTeamUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    suspend fun execute(teamId: Int) = teamRepository.getTeam(teamId)
}