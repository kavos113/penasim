package com.example.penasim.core.di

import com.example.penasim.core.session.InMemorySelectedTeamStore
import com.example.penasim.core.session.SelectedTeamStore
import com.example.penasim.features.game.domain.TransactionProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.penasim.core.database.TransactionProvider as TransactionProviderImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class ProviderModule {
  @Binds
  @Singleton
  abstract fun bindTransactionProvider(
    impl: TransactionProviderImpl
  ): TransactionProvider

  @Binds
  @Singleton
  abstract fun bindSelectedTeamStore(
    impl: InMemorySelectedTeamStore
  ): SelectedTeamStore
}

