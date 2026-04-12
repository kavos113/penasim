package com.example.penasim.features.schedule.domain.repository

import com.example.penasim.features.schedule.domain.GameFixture
import com.example.penasim.features.team.domain.Team
import java.time.LocalDate

interface GameFixtureRepository {
  suspend fun getGameFixture(id: Int): GameFixture?
  suspend fun getGameFixturesByDate(date: LocalDate): List<GameFixture>
  suspend fun getGameFixturesByTeam(team: Team): List<GameFixture>
  suspend fun getAllGameFixtures(): List<GameFixture>
}