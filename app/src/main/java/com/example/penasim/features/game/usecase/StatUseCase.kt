package com.example.penasim.features.game.usecase

import com.example.penasim.domain.Stat
import com.example.penasim.domain.repository.StatRepository
import javax.inject.Inject

class StatUseCase @Inject constructor(
  private val repository: StatRepository
) {
  suspend fun getByFixtureId(fixtureId: Int): List<Stat> =
    repository.getByFixtureId(fixtureId)

  suspend fun getByBatterId(batterId: Int): List<Stat> =
    repository.getByBatterId(batterId)

  suspend fun getByPitcherId(pitcherId: Int): List<Stat> =
    repository.getByPitcherId(pitcherId)

  suspend fun getByFixtureIdAndBatterId(fixtureId: Int, batterId: Int): List<Stat> =
    repository.getByFixtureIdAndBatterId(fixtureId, batterId)

  suspend fun getByFixtureIdAndPitcherId(fixtureId: Int, pitcherId: Int): List<Stat> =
    repository.getByFixtureIdAndPitcherId(fixtureId, pitcherId)

  suspend fun insertAll(items: List<Stat>) {
    if (items.isEmpty()) return
    repository.insertAll(items)
  }

  suspend fun deleteByFixtureId(fixtureId: Int) =
    repository.deleteByFixtureId(fixtureId)
}
