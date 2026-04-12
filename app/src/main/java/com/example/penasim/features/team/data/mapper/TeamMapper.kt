package com.example.penasim.data.mapper

import com.example.penasim.data.entity.TeamEntity
import com.example.penasim.domain.Team
import com.example.penasim.domain.toId
import com.example.penasim.domain.toLeague

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