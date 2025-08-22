package com.example.penasim.data.repository

import com.example.penasim.data.dao.TeamDao
import com.example.penasim.data.mapper.toDomain
import com.example.penasim.domain.League
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.TeamRepository
import com.example.penasim.domain.toId

class TeamRepository(
    private val teamDao: TeamDao
): TeamRepository {
    override suspend fun getTeam(id: Int): Team? = teamDao.getById(id)?.toDomain()

    override suspend fun getTeamsByLeague(league: League): List<Team>
        = teamDao.getByLeagueId(league.toId()).map { it.toDomain() }

    override suspend fun getAllTeams(): List<Team>
        = teamDao.getAll().map { it.toDomain() }
}