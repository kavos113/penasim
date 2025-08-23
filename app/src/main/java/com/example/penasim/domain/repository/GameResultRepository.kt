package com.example.penasim.domain.repository

import com.example.penasim.domain.GameResult

interface GameResultRepository {
    suspend fun getGameByFixtureId(fixtureId: Int): GameResult?
    suspend fun getGamesByFixtureIds(fixtureIds: List<Int>): List<GameResult>
    suspend fun getAllGames(): List<GameResult>

    suspend fun createGame(fixtureId: Int, homeScore: Int, awayScore: Int): GameResult?
}