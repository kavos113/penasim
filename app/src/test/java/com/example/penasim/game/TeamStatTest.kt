package com.example.penasim.game

import com.example.penasim.domain.HomeRun
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Test

class TeamStatTest {

    @Test
    fun newInning_and_score_updatesAwaySide() {
        val ts = TeamStat(fixtureId = 1)
        // 初期状態: awayScores は [0]
        assertEquals(listOf(0), ts.awayScores)

        // 表のイニング開始（2回表として0を追加）
        ts.newInning(Half.INNING_TOP)
        assertEquals(listOf(0, 0), ts.awayScores)

        // 表で2点
        ts.score(batterId = 10, pitcherId = 20, half = Half.INNING_TOP, count = 2)
        assertEquals(2, ts.awayScore)
        assertEquals(listOf(0, 2), ts.awayScores)

        // 打点と投手の失点が付く
        val b = ts.battingStats[10]!!
        assertEquals(2, b.rbi)
        val p = ts.pitchingStats[20]!!
        assertEquals(2, p.run)
        assertEquals(2, p.earnedRun)
    }

    @Test
    fun newInning_and_score_updatesHomeSide() {
        val ts = TeamStat(fixtureId = 2)

        // 裏のイニング開始（1回裏）
        ts.newInning(Half.INNING_BOTTOM)
        assertEquals(listOf(0), ts.homeScores)

        // 裏で1点
        ts.score(batterId = 11, pitcherId = 21, half = Half.INNING_BOTTOM, count = 1)
        assertEquals(1, ts.homeScore)
        assertEquals(listOf(1), ts.homeScores)
    }

    @Test
    fun out_createsPitchingStatWithOneInningPitched() {
        val ts = TeamStat(fixtureId = 3)
        ts.out(batterId = 1, pitcherId = 99)

        val batter = ts.battingStats[1]!!
        assertEquals(1, batter.atBat)
        val pitcher = ts.pitchingStats[99]!!
        // 現実的には 1/3 イニング等だが、現在の実装は 1 をセット
        assertEquals(1, pitcher.inningPitched)
    }

    @Test
    fun single_double_triple_updateBothBattingAndPitchingStats() {
        val ts = TeamStat(fixtureId = 4)
        val batter = 5
        val pitcher = 6

        ts.single(batterId = batter, pitcherId = pitcher)
        ts.double(batterId = batter, pitcherId = pitcher)
        ts.triple(batterId = batter, pitcherId = pitcher)

        val bs = ts.battingStats[batter]!!
        assertEquals(3, bs.atBat)
        assertEquals(3, bs.hit)
        assertEquals(1, bs.doubleHit)
        assertEquals(1, bs.tripleHit)

        val ps = ts.pitchingStats[pitcher]!!
        assertEquals(3, ps.hit)
    }

    @Test
    fun homeRun_recordsToProperSide_andUpdatesStats() {
        val ts = TeamStat(fixtureId = 5)

        // 1回表を開始（away side は初期 [0] があり、追加で 0 を入れる想定のケースもあるが、記録はどちらでも可）
        // ここでは本塁打の記録先のみ検証
        ts.homeRun(batterId = 7, pitcherId = 8, half = Half.INNING_TOP, inning = 1, count = 1)
        assertEquals(1, ts.awayHomeRuns.size)
        assertEquals(0, ts.homeHomeRuns.size)

        val hr: HomeRun = ts.awayHomeRuns.first()
        assertEquals(5, hr.fixtureId)
        assertEquals(7, hr.playerId)
        assertEquals(1, hr.inning)
        assertEquals(1, hr.count)

        val bs = ts.battingStats[7]!!
        assertEquals(1, bs.atBat)
        assertEquals(1, bs.hit)
        assertEquals(1, bs.homeRun)

        val ps = ts.pitchingStats[8]!!
        assertEquals(1, ps.hit)
        assertEquals(1, ps.homeRun)
    }

    @Test
    fun inningScores_returnsHomeAndAwayCombined() {
        val ts = TeamStat(fixtureId = 6)
        // 1回表: away に1点
        ts.score(batterId = 1, pitcherId = 2, half = Half.INNING_TOP, count = 1)
        // 1回裏: home スコア用にイニングを作って1点
        ts.newInning(Half.INNING_BOTTOM)
        ts.score(batterId = 3, pitcherId = 4, half = Half.INNING_BOTTOM, count = 1)

        val scores = ts.inningScores(homeTeamId = 100, awayTeamId = 200)

        // home 1件, away 1件 の計2件
        assertEquals(2, scores.size)
        val home = scores.first { it.teamId == 100 }
        val away = scores.first { it.teamId == 200 }
        assertEquals(1, home.inning)
        assertEquals(1, away.inning)
        assertEquals(1, home.score)
        assertEquals(1, away.score)
    }

    @Test
    fun finalize_setsWinLose_andRemovesLastAwayInning() {
        val ts = TeamStat(fixtureId = 7)

        // 試合経過を作る
        // 1回表: away 1点
        ts.score(batterId = 10, pitcherId = 2000, half = Half.INNING_TOP, count = 1)
        // 1回裏: home 用にイニングを追加して2点（home がリード）
        ts.newInning(Half.INNING_BOTTOM)
        ts.score(batterId = 11, pitcherId = 1000, half = Half.INNING_BOTTOM, count = 2)

        // finalize で勝敗フラグを付けるため、両投手のエントリを作成
        ts.out(batterId = 99, pitcherId = 1000) // home 投手
        ts.out(batterId = 98, pitcherId = 2000) // away 投手

        val awayScoresBefore = ts.awayScores.toList()
        assertTrue(awayScoresBefore.isNotEmpty())

        ts.finalize(homePitcherId = 1000, awayPitcherId = 2000)

        // awayScores の末尾が削除されている
        assertEquals(awayScoresBefore.dropLast(1), ts.awayScores)

        // home が 2-1 で勝利 → homePitcher win, awayPitcher lose
        assertTrue(ts.pitchingStats[1000]!!.win)
        assertTrue(ts.pitchingStats[2000]!!.lose)
    }
}
