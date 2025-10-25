package com.example.penasim.data.di

import com.example.penasim.domain.TransactionProvider
import com.example.penasim.data.TransactionProvider as TransactionProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProviderModule {
    @Binds
    @Singleton
    abstract fun bindTransactionProvider(
        impl: TransactionProviderImpl
    ): TransactionProvider
}

