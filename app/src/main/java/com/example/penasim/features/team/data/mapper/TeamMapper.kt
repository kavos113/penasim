package com.example.penasim.features.team.data.mapper

import com.example.penasim.features.team.data.entity.TeamEntity
import com.example.penasim.features.team.domain.Team
import com.example.penasim.features.team.domain.toId
import com.example.penasim.features.team.domain.toLeague

fun TeamEntity.toDomain(): Team = Team(
  id = id,
  name = name,
  league = leagueId.toLeague()
)

fun Team.toEntity(): TeamEntity = TeamEntity(
  id = id,
  name = name,
  leagueId = league.toId()
)