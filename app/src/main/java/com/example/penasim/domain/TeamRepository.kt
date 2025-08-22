package com.example.penasim.domain

interface TeamRepository {
    suspend fun getTeam(id: Int): Team?
    suspend fun getTeamsByLeague(league: League): List<Team>
    suspend fun getAllTeams(): List<Team>
}