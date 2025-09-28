package com.example.penasim.usecase

import com.example.penasim.domain.repository.MainMembersRepository
import javax.inject.Inject

class GetMainMembersByTeamUseCase @Inject constructor(
    private val mainMembersRepository: MainMembersRepository
) {
    suspend fun execute(teamId: Int) = mainMembersRepository.getMainMembersByTeamId(teamId)
}