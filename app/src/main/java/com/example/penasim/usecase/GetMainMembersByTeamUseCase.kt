package com.example.penasim.usecase

import com.example.penasim.domain.MainMember
import javax.inject.Inject

class GetMainMembersByTeamUseCase @Inject constructor(
    private val mainMembersUseCase: MainMembersUseCase
) {
    suspend fun execute(teamId: Int): List<MainMember> =
        mainMembersUseCase.getByTeamId(teamId)
}