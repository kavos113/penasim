package com.example.penasim.data.di

import com.example.penasim.domain.repository.GameResultRepository
import com.example.penasim.data.repository.GameResultRepository as GameResultRepositoryImpl
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.data.repository.GameFixtureRepository as GameFixtureRepositoryImpl
import com.example.penasim.domain.repository.TeamRepository
import com.example.penasim.data.repository.TeamRepository as TeamRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTeamRepository(
        impl: TeamRepositoryImpl
    ): TeamRepository

    @Binds
    @Singleton
    abstract fun bindGameFixtureRepository(
        impl: GameFixtureRepositoryImpl
    ): GameFixtureRepository

    @Binds
    @Singleton
    abstract fun bindGameResultRepository(
        impl: GameResultRepositoryImpl
    ): GameResultRepository
}