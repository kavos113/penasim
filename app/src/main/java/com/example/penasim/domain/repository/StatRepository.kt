package com.example.penasim.domain.repository

import com.example.penasim.domain.Stat

interface StatRepository {
  suspend fun getByFixtureId(fixtureId: Int): List<Stat>
  suspend fun getByBatterId(batterId: Int): List<Stat>
  suspend fun getByPitcherId(pitcherId: Int): List<Stat>
  suspend fun getByFixtureIdAndBatterId(fixtureId: Int, batterId: Int): List<Stat>
  suspend fun getByFixtureIdAndPitcherId(fixtureId: Int, pitcherId: Int): List<Stat>
  suspend fun insertAll(items: List<Stat>)
  suspend fun deleteByFixtureId(fixtureId: Int)
}
