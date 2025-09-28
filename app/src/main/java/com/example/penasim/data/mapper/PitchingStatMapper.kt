package com.example.penasim.data.mapper

import com.example.penasim.data.entity.PitchingStatEntity
import com.example.penasim.domain.PitchingStat

fun PitchingStatEntity.toDomain(): PitchingStat = PitchingStat(
    gameFixtureId = gameFixtureId,
    playerId = playerId,
    inningPitched = inningPitched,
    hit = hit,
    run = run,
    earnedRun = earnedRun,
    walk = walk,
    strikeOut = strikeOut,
    homeRun = homeRun,
    win = win,
    lose = loss,
    hold = hold,
    save = save,
)

fun PitchingStat.toEntity(): PitchingStatEntity = PitchingStatEntity(
    gameFixtureId = gameFixtureId,
    playerId = playerId,
    inningPitched = inningPitched,
    hit = hit,
    run = run,
    earnedRun = earnedRun,
    walk = walk,
    strikeOut = strikeOut,
    homeRun = homeRun,
    win = win,
    loss = lose,
    hold = hold,
    save = save,
)
