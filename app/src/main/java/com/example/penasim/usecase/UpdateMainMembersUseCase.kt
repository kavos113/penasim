package com.example.penasim.usecase

import com.example.penasim.domain.MainMember
import javax.inject.Inject

class UpdateMainMembersUseCase @Inject constructor(
    private val mainMembersUseCase: MainMembersUseCase
) {
    suspend fun execute(members: List<MainMember>) {
        mainMembersUseCase.updateTeamMembers(members)
    }
}