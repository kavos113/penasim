package com.example.penasim.data.di

import android.content.Context
import androidx.room.Room
import com.example.penasim.data.PennantDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): PennantDatabase {
        return Room.databaseBuilder(
            context,
            PennantDatabase::class.java,
            "pennant_database"
        )
            .createFromAsset("databases/pennant_database.db")
            .build()
    }

    @Provides
    fun provideTeamDao(database: PennantDatabase) = database.teamDao()

    @Provides
    fun provideGameFixtureDao(database: PennantDatabase) = database.gameFixtureDao()

    @Provides
    fun provideGameResultDao(database: PennantDatabase) = database.gameResultDao()
}