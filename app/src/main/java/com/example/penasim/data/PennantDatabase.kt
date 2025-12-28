package com.example.penasim.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.penasim.data.dao.BattingStatDao
import com.example.penasim.data.dao.FielderAppointmentDao
import com.example.penasim.data.dao.GameFixtureDao
import com.example.penasim.data.dao.GameResultDao
import com.example.penasim.data.dao.HomeRunDao
import com.example.penasim.data.dao.InningScoreDao
import com.example.penasim.data.dao.MainMemberDao
import com.example.penasim.data.dao.PitcherAppointmentDao
import com.example.penasim.data.dao.PitchingStatDao
import com.example.penasim.data.dao.PlayerDao
import com.example.penasim.data.dao.PlayerPositionDao
import com.example.penasim.data.dao.TeamDao
import com.example.penasim.data.entity.BattingStatEntity
import com.example.penasim.data.entity.FielderAppointmentEntity
import com.example.penasim.data.entity.GameFixtureEntity
import com.example.penasim.data.entity.GameResultEntity
import com.example.penasim.data.entity.HomeRunEntity
import com.example.penasim.data.entity.InningScoreEntity
import com.example.penasim.data.entity.MainMemberEntity
import com.example.penasim.data.entity.PitcherAppointmentEntity
import com.example.penasim.data.entity.PitchingStatEntity
import com.example.penasim.data.entity.PlayerEntity
import com.example.penasim.data.entity.PlayerPositionEntity
import com.example.penasim.data.entity.TeamEntity
import com.example.penasim.data.repository.Converters

@Database(
  entities = [
    TeamEntity::class,
    GameFixtureEntity::class,
    GameResultEntity::class,
    PlayerEntity::class,
    PlayerPositionEntity::class,
    FielderAppointmentEntity::class,
    PitcherAppointmentEntity::class,
    MainMemberEntity::class,
    InningScoreEntity::class,
    BattingStatEntity::class,
    PitchingStatEntity::class,
    HomeRunEntity::class,
  ],
  version = 11,
  exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PennantDatabase : RoomDatabase() {
  abstract fun teamDao(): TeamDao
  abstract fun gameFixtureDao(): GameFixtureDao
  abstract fun gameResultDao(): GameResultDao
  abstract fun playerDao(): PlayerDao
  abstract fun playerPositionDao(): PlayerPositionDao
  abstract fun fielderAppointmentDao(): FielderAppointmentDao
  abstract fun pitcherAppointmentDao(): PitcherAppointmentDao
  abstract fun mainMemberDao(): MainMemberDao
  abstract fun inningScoreDao(): InningScoreDao
  abstract fun battingStatDao(): BattingStatDao
  abstract fun pitchingStatDao(): PitchingStatDao
  abstract fun homeRunDao(): HomeRunDao
}