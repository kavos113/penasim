package com.example.penasim.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.penasim.data.dao.*
import com.example.penasim.data.entity.*
import com.example.penasim.data.repository.Converters

@Database(
    entities = [
        TeamEntity::class,
        GameFixtureEntity::class,
        GameResultEntity::class,
        PlayerEntity::class,
        PlayerPositionEntity::class,
        FielderAppointmentEntity::class,
        PitcherAppointmentEntity::class,
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class PennantDatabase : RoomDatabase() {
    abstract fun teamDao(): TeamDao
    abstract fun gameFixtureDao(): GameFixtureDao
    abstract fun gameResultDao(): GameResultDao
    abstract fun playerDao(): PlayerDao
    abstract fun playerPositionDao(): PlayerPositionDao
    abstract fun fielderAppointmentDao(): FielderAppointmentDao
    abstract fun pitcherAppointmentDao(): PitcherAppointmentDao
}