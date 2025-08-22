package com.example.penasim.data.mapper

import com.example.penasim.data.entity.GameMasterEntity
import com.example.penasim.domain.GameMaster
import com.example.penasim.domain.Team

fun GameMasterEntity.toDomain(
    homeTeam: Team,
    awayTeam: Team
): GameMaster = GameMaster(
    id = id,
    date = date,
    numberOfGames = numberOfGames,
    homeTeam = homeTeam,
    awayTeam = awayTeam
)

fun GameMaster.toEntity(): GameMasterEntity = GameMasterEntity(
    id = id,
    date = date,
    numberOfGames = numberOfGames,
    homeTeamId = homeTeam.id,
    awayTeamId = awayTeam.id
)