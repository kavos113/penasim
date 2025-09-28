package com.example.penasim.domain.repository

import com.example.penasim.domain.InningScore

interface InningScoreRepository {
    suspend fun getByFixtureId(fixtureId: Int): List<InningScore>
    suspend fun getByFixtureIds(fixtureIds: List<Int>): List<InningScore>
    suspend fun getByTeamId(teamId: Int): List<InningScore>
    suspend fun getByTeamIds(teamIds: List<Int>): List<InningScore>
    suspend fun insertAll(items: List<InningScore>)
    suspend fun deleteByFixtureId(fixtureId: Int)
}
