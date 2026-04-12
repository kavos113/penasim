package com.example.penasim.features.game.domain

interface TransactionProvider {
  suspend fun <T> runInTransaction(block: suspend () -> T): T
}