package com.example.penasim.repository

import androidx.room.Database
import com.example.penasim.model.GameMaster
import com.example.penasim.model.GameMasterDao

@Database(
    entities = [GameMaster::class],
    version = 1,
    exportSchema = false
)
abstract class PennantDatabase {
    abstract fun gameMasterDao(): GameMasterDao
}