package com.example.penasim.domain.repository

import com.example.penasim.domain.GameResult

interface GameRepository {
    suspend fun getGame(id: Int): GameResult?
    suspend fun getGameByFixtureId(fixtureId: Int): GameResult?
    suspend fun getGamesByFixtureIds(fixtureIds: List<Int>): List<GameResult>
    suspend fun getAllGames(): List<GameResult>

    suspend fun createGame(masterId: Int, homeScore: Int, awayScore: Int): GameResult?
}