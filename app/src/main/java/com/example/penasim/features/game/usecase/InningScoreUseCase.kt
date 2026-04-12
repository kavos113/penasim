package com.example.penasim.features.game.usecase

import com.example.penasim.features.game.domain.InningScore
import com.example.penasim.features.game.domain.repository.InningScoreRepository
import javax.inject.Inject

class InningScoreUseCase @Inject constructor(
  private val repository: InningScoreRepository
) {
  suspend fun getByFixtureId(fixtureId: Int): List<InningScore> =
    repository.getByFixtureId(fixtureId)

  suspend fun getByFixtureIds(fixtureIds: List<Int>): List<InningScore> =
    repository.getByFixtureIds(fixtureIds)

  suspend fun getByTeamId(teamId: Int): List<InningScore> = repository.getByTeamId(teamId)
  suspend fun getByTeamIds(teamIds: List<Int>): List<InningScore> = repository.getByTeamIds(teamIds)

  suspend fun insertAll(items: List<InningScore>) {
    if (items.isEmpty()) return
    repository.insertAll(items)
  }
}