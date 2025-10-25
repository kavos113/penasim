package com.example.penasim.usecase

import com.example.penasim.domain.InningScore
import javax.inject.Inject

class GetInningScoreUseCase @Inject constructor(
    private val inningScoreUseCase: InningScoreUseCase
) {
    suspend fun executeByFixtureId(fixtureId: Int): List<InningScore> =
        inningScoreUseCase.getByFixtureId(fixtureId)

    suspend fun executeByFixtureIds(fixtureIds: List<Int>): List<InningScore> =
        inningScoreUseCase.getByFixtureIds(fixtureIds)

    suspend fun executeByTeamId(teamId: Int): List<InningScore> =
        inningScoreUseCase.getByTeamId(teamId)

    suspend fun executeByTeamIds(teamIds: List<Int>): List<InningScore> =
        inningScoreUseCase.getByTeamIds(teamIds)
}
