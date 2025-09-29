package com.example.penasim.usecase

import com.example.penasim.domain.InningScore
import com.example.penasim.domain.repository.InningScoreRepository
import javax.inject.Inject

class GetInningScoreUseCase @Inject constructor(
    private val inningScoreRepository: InningScoreRepository
) {
    suspend fun executeByFixtureId(fixtureId: Int): List<InningScore> =
        inningScoreRepository.getByFixtureId(fixtureId)

    suspend fun executeByFixtureIds(fixtureIds: List<Int>): List<InningScore> =
        inningScoreRepository.getByFixtureIds(fixtureIds)

    suspend fun executeByTeamId(teamId: Int): List<InningScore> =
        inningScoreRepository.getByTeamId(teamId)

    suspend fun executeByTeamIds(teamIds: List<Int>): List<InningScore> =
        inningScoreRepository.getByTeamIds(teamIds)
}
