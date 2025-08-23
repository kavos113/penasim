package com.example.penasim.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.penasim.data.dao.GameFixtureDao
import com.example.penasim.data.dao.GameResultDao
import com.example.penasim.data.dao.TeamDao
import com.example.penasim.data.entity.GameFixtureEntity
import com.example.penasim.data.entity.GameResultEntity
import com.example.penasim.data.entity.TeamEntity
import com.example.penasim.data.repository.Converters

@Database(
    entities = [TeamEntity::class, GameFixtureEntity::class, GameResultEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class PennantDatabase : RoomDatabase() {
    abstract fun teamDao(): TeamDao
    abstract fun gameFixtureDao(): GameFixtureDao
    abstract fun gameResultDao(): GameResultDao
}