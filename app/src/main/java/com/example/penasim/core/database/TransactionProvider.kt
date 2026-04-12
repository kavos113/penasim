package com.example.penasim.core.database

import androidx.room.withTransaction
import com.example.penasim.features.game.domain.TransactionProvider
import javax.inject.Inject

class TransactionProvider @Inject constructor(
  private val db: PennantDatabase
) : TransactionProvider {
  override suspend fun <T> runInTransaction(block: suspend () -> T): T {
    return db.withTransaction {
      block()
    }
  }
}