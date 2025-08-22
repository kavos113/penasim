package com.example.penasim.domain

interface GameRepository {
    suspend fun getGame(id: Int): Game?
    suspend fun getGamesByDate(date: Schedule): List<Game>
    suspend fun getGamesByTeam(team: Team): List<Game>
    suspend fun getFinishedGamesByTeam(team: Team): List<Game>
    suspend fun getGamesByLeague(league: League): List<Game>
    suspend fun getAllGames(): List<Game>

    suspend fun recordGameResult(gameId: Int, homeScore: Int, awayScore: Int): Boolean
}