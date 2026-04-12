package com.example.penasim.domain

interface TransactionProvider {
  suspend fun <T> runInTransaction(block: suspend () -> T): T
}