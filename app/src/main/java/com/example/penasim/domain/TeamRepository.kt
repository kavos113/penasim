package com.example.penasim.domain

interface TeamRepository {
    fun getTeam(id: Int): Team?
    fun getTeamsByLeague(league: League): List<Team>
    fun getAllTeams(): List<Team>
}