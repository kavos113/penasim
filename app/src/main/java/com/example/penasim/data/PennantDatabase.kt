package com.example.penasim.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.penasim.data.dao.GameFixtureDao
import com.example.penasim.data.dao.GameResultDao
import com.example.penasim.data.dao.TeamDao
import com.example.penasim.data.entity.GameFixtureEntity
import com.example.penasim.data.entity.GameResultEntity
import com.example.penasim.data.entity.TeamEntity

@Database(
    entities = [TeamEntity::class, GameFixtureEntity::class, GameResultEntity::class],
    version = 1,
    exportSchema = true
)
abstract class PennantDatabase : RoomDatabase() {
    abstract fun teamDao(): TeamDao
    abstract fun gameFixtureDao(): GameFixtureDao
    abstract fun gameResultDao(): GameResultDao
}