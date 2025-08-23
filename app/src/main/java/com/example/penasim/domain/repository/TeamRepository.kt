package com.example.penasim.domain.repository

import com.example.penasim.domain.League
import com.example.penasim.domain.Team

interface TeamRepository {
    suspend fun getTeam(id: Int): Team?
    suspend fun getTeamsByLeague(league: League): List<Team>
    suspend fun getAllTeams(): List<Team>
}