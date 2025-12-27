package com.example.penasim.usecase

import com.example.penasim.domain.HomeRun
import com.example.penasim.domain.repository.HomeRunRepository
import javax.inject.Inject

class HomeRunUseCase @Inject constructor(
  private val homeRunRepository: HomeRunRepository
) {
  suspend fun getByFixtureId(fixtureId: Int): List<HomeRun> =
    homeRunRepository.getHomeRunsByFixtureId(fixtureId)

  suspend fun getByPlayerId(playerId: Int): List<HomeRun> =
    homeRunRepository.getHomeRunsByPlayerId(playerId)

  suspend fun insert(homeRuns: List<HomeRun>) {
    if (homeRuns.isEmpty()) return
    homeRunRepository.insertHomeRuns(homeRuns)
  }
}
