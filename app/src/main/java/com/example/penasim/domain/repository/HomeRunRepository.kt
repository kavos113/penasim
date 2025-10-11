package com.example.penasim.domain.repository

import com.example.penasim.domain.HomeRun

interface HomeRunRepository {
    suspend fun getHomeRunsByFixtureId(fixtureId: Int): List<HomeRun>
    suspend fun getHomeRunsByPlayerId(playerId: Int): List<HomeRun>
    suspend fun insertHomeRuns(homeRuns: List<HomeRun>)
}