package com.example.penasim.features.team.domain.repository

import com.example.penasim.features.team.domain.League
import com.example.penasim.features.team.domain.Team

interface TeamRepository {
  suspend fun getTeam(id: Int): Team?
  suspend fun getTeamsByLeague(league: League): List<Team>
  suspend fun getAllTeams(): List<Team>
}