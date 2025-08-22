package com.example.penasim.data.mapper

import com.example.penasim.data.entity.GameMasterEntity
import com.example.penasim.domain.Date
import com.example.penasim.domain.GameMaster
import com.example.penasim.domain.Team

fun GameMasterEntity.toDomain(
    date: Date,
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
    dateId = date.id,
    numberOfGames = numberOfGames,
    homeTeamId = homeTeam.id,
    awayTeamId = awayTeam.id
)