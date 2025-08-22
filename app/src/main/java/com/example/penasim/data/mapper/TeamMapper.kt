package com.example.penasim.data.mapper

import com.example.penasim.data.entity.TeamEntity
import com.example.penasim.domain.League
import com.example.penasim.domain.Team

fun TeamEntity.toDomain(): Team = Team(
    id = id,
    name = name,
    league = League.fromId(leagueId)
)

fun Team.toEntity(): TeamEntity = TeamEntity(
    id = id,
    name = name,
    leagueId = League.toId(league)
)