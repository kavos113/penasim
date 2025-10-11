package com.example.penasim.data.di

import com.example.penasim.domain.repository.GameResultRepository
import com.example.penasim.data.repository.GameResultRepository as GameResultRepositoryImpl
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.data.repository.GameFixtureRepository as GameFixtureRepositoryImpl
import com.example.penasim.domain.repository.TeamRepository
import com.example.penasim.data.repository.TeamRepository as TeamRepositoryImpl
import com.example.penasim.domain.repository.PlayerRepository
import com.example.penasim.data.repository.PlayerRepository as PlayerRepositoryImpl
import com.example.penasim.domain.repository.PlayerPositionRepository
import com.example.penasim.data.repository.PlayerPositionRepository as PlayerPositionRepositoryImpl
import com.example.penasim.domain.repository.FielderAppointmentRepository
import com.example.penasim.data.repository.FielderAppointmentRepository as FielderAppointmentRepositoryImpl
import com.example.penasim.domain.repository.PitcherAppointmentRepository
import com.example.penasim.data.repository.PitcherAppointmentRepository as PitcherAppointmentRepositoryImpl
import com.example.penasim.domain.repository.MainMembersRepository
import com.example.penasim.data.repository.MainMembersRepository as MainMembersRepositoryImpl
import com.example.penasim.domain.repository.InningScoreRepository
import com.example.penasim.data.repository.InningScoreRepository as InningScoreRepositoryImpl
import com.example.penasim.domain.repository.BattingStatRepository
import com.example.penasim.data.repository.BattingStatRepository as BattingStatRepositoryImpl
import com.example.penasim.domain.repository.PitchingStatRepository
import com.example.penasim.data.repository.PitchingStatRepository as PitchingStatRepositoryImpl
import com.example.penasim.domain.repository.HomeRunRepository
import com.example.penasim.data.repository.HomeRunRepository as HomeRunRepositoryImpl
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

    @Binds
    @Singleton
    abstract fun bindPlayerRepository(
        impl: PlayerRepositoryImpl
    ): PlayerRepository

    @Binds
    @Singleton
    abstract fun bindPlayerPositionRepository(
        impl: PlayerPositionRepositoryImpl
    ): PlayerPositionRepository

    @Binds
    @Singleton
    abstract fun bindFielderAppointmentRepository(
        impl: FielderAppointmentRepositoryImpl
    ): FielderAppointmentRepository

    @Binds
    @Singleton
    abstract fun bindPitcherAppointmentRepository(
        impl: PitcherAppointmentRepositoryImpl
    ): PitcherAppointmentRepository

    @Binds
    @Singleton
    abstract fun bindMainMembersRepository(
        impl: MainMembersRepositoryImpl
    ): MainMembersRepository

    @Binds
    @Singleton
    abstract fun bindInningScoreRepository(
        impl: InningScoreRepositoryImpl
    ): InningScoreRepository

    @Binds
    @Singleton
    abstract fun bindBattingStatRepository(
        impl: BattingStatRepositoryImpl
    ): BattingStatRepository

    @Binds
    @Singleton
    abstract fun bindPitchingStatRepository(
        impl: PitchingStatRepositoryImpl
    ): PitchingStatRepository

    @Binds
    @Singleton
    abstract fun bindHomeRunRepository(
        impl: HomeRunRepositoryImpl
    ): HomeRunRepository
}