package com.example.penasim.domain

interface GameRepository {
    fun getGame(id: Int): Game?
    fun getGamesByDate(date: Schedule): List<Game>
    fun getGamesByTeam(team: Team): List<Game>
    fun getGamesByLeague(league: League): List<Game>
    fun getAllGames(): List<Game>

    fun recordGameResult(gameId: Int, homeScore: Int, awayScore: Int): Boolean
}