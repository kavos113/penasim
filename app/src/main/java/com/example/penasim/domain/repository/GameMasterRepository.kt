package com.example.penasim.domain.repository

import com.example.penasim.domain.Date
import com.example.penasim.domain.GameMaster
import com.example.penasim.domain.Team

interface GameMasterRepository {
    suspend fun getGameMaster(id: Int): GameMaster?
    suspend fun getGameMastersByDate(date: Date): List<GameMaster>
    suspend fun getGameMastersByTeam(team: Team): List<GameMaster>
    suspend fun getAllGameMasters(): List<GameMaster>
}