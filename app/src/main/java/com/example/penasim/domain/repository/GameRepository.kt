package com.example.penasim.domain.repository

import com.example.penasim.domain.Date
import com.example.penasim.domain.Game
import com.example.penasim.domain.League
import com.example.penasim.domain.Team

interface GameRepository {
    suspend fun getGame(id: Int): Game?
    suspend fun getGamesByDate(date: Date): List<Game>
    suspend fun getGamesByTeam(team: Team): List<Game>
    suspend fun getFinishedGamesByTeam(team: Team): List<Game>
    suspend fun getGamesByLeague(league: League): List<Game>
    suspend fun getAllGames(): List<Game>
}