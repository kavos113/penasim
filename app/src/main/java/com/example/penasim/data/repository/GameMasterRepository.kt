package com.example.penasim.data.repository

import com.example.penasim.data.dao.GameMasterDao
import com.example.penasim.domain.Date
import com.example.penasim.domain.GameMaster
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.GameMasterRepository

class GameMasterRepository(
    private val gameMasterDao: GameMasterDao,
): GameMasterRepository {
    override suspend fun getGameMaster(id: Int): GameMaster? {

    }

    override suspend fun getGameMastersByDate(date: Date): List<GameMaster> {
        TODO("Not yet implemented")
    }

    override suspend fun getGameMastersByTeam(team: Team): List<GameMaster> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllGameMasters(): List<GameMaster> {
        TODO("Not yet implemented")
    }
}