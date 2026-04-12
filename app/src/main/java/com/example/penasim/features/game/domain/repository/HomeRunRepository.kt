package com.example.penasim.features.game.domain.repository

import com.example.penasim.features.game.domain.HomeRun

interface HomeRunRepository {
  suspend fun getHomeRunsByFixtureId(fixtureId: Int): List<HomeRun>
  suspend fun getHomeRunsByPlayerId(playerId: Int): List<HomeRun>
  suspend fun insertHomeRuns(homeRuns: List<HomeRun>)
}