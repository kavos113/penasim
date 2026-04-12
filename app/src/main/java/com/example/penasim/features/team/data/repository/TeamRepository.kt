package com.example.penasim.features.team.data.repository

import com.example.penasim.features.team.data.dao.TeamDao
import com.example.penasim.features.team.data.mapper.toDomain
import com.example.penasim.features.team.domain.League
import com.example.penasim.features.team.domain.Team
import com.example.penasim.features.team.domain.repository.TeamRepository
import com.example.penasim.features.team.domain.toId
import javax.inject.Inject

class TeamRepository @Inject constructor(
  private val teamDao: TeamDao
) : TeamRepository {
  override suspend fun getTeam(id: Int): Team? = teamDao.getById(id)?.toDomain()

  override suspend fun getTeamsByLeague(league: League): List<Team> =
    teamDao.getByLeagueId(league.toId()).map { it.toDomain() }

  override suspend fun getAllTeams(): List<Team> = teamDao.getAll().map { it.toDomain() }
}