package com.example.penasim.usecase

import com.example.penasim.domain.BattingStat
import com.example.penasim.domain.repository.BattingStatRepository
import javax.inject.Inject

class BattingStatUseCase @Inject constructor(
  private val repository: BattingStatRepository
) {
  suspend fun getByFixtureId(fixtureId: Int): List<BattingStat> =
    repository.getByFixtureId(fixtureId)

  suspend fun getByFixtureIds(fixtureIds: List<Int>): List<BattingStat> =
    repository.getByFixtureIds(fixtureIds)

  suspend fun getByPlayerId(playerId: Int): List<BattingStat> = repository.getByPlayerId(playerId)
  suspend fun getByPlayerIds(playerIds: List<Int>): List<BattingStat> =
    repository.getByPlayerIds(playerIds)

  suspend fun insertAll(items: List<BattingStat>) {
    if (items.isEmpty()) return
    repository.insertAll(items)
  }

  suspend fun deleteByFixtureId(fixtureId: Int) = repository.deleteByFixtureId(fixtureId)
}
