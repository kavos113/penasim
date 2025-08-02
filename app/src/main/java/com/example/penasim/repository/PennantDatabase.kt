package com.example.penasim.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.penasim.model.GameMaster
import com.example.penasim.model.GameMasterDao

@Database(
    entities = [GameMaster::class],
    version = 1,
    exportSchema = true
)
abstract class PennantDatabase : RoomDatabase() {
    abstract fun gameMasterDao(): GameMasterDao

    companion object {
        @Volatile
        private var INSTANCE: PennantDatabase? = null

        fun getDatabase(context: android.content.Context): PennantDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    PennantDatabase::class.java,
                    "pennant_database"
                ).createFromAsset("databases/initial_data.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}