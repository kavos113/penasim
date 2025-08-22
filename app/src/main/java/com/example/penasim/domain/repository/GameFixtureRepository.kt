package com.example.penasim.domain.repository

import com.example.penasim.domain.GameFixture
import com.example.penasim.domain.Team
import java.time.LocalDate

interface GameFixtureRepository {
    suspend fun getGameMaster(id: Int): GameFixture?
    suspend fun getGameMastersByDate(date: LocalDate): List<GameFixture>
    suspend fun getGameMastersByTeam(team: Team): List<GameFixture>
    suspend fun getAllGameMasters(): List<GameFixture>
}