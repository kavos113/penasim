package com.example.penasim.domain.repository

import com.example.penasim.domain.GameFixture
import com.example.penasim.domain.Team
import java.time.LocalDate

interface GameFixtureRepository {
  suspend fun getGameFixture(id: Int): GameFixture?
  suspend fun getGameFixturesByDate(date: LocalDate): List<GameFixture>
  suspend fun getGameFixturesByTeam(team: Team): List<GameFixture>
  suspend fun getAllGameFixtures(): List<GameFixture>
}