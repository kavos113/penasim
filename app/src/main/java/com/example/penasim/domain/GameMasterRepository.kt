package com.example.penasim.domain

interface GameMasterRepository {
    suspend fun getGameMaster(id: Int): GameMaster?
    suspend fun getGameMastersByDate(date: Date): List<GameMaster>
    suspend fun getGameMastersByTeam(team: Team): List<GameMaster>
    suspend fun getAllGameMasters(): List<GameMaster>
}