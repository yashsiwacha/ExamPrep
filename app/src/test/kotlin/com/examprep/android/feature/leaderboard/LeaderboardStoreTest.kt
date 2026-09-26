package com.examprep.android.feature.leaderboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LeaderboardStoreTest {

    private lateinit var store: LeaderboardStore

    @Before
    fun setUp() {
        store = LeaderboardStore()
    }

    @Test
    fun testDefaultReportIntegrity() {
        val report = store.report.value
        assertNotNull(report)
        assertTrue(report.totalParticipants > 0)
        assertTrue(report.userRank in 1..report.totalParticipants)
        assertTrue(report.userPercentile in 0.0f..100.0f)
        assertTrue(report.topperScore >= report.userScore)
        assertTrue(report.averageScore <= report.topperScore)
    }

    @Test
    fun testLeaderboardOrder() {
        val list = store.report.value.leaderboard
        assertTrue(list.isNotEmpty())
        val topper = list.first()
        assertEquals(1, topper.rank)
        
        // Candidate student must be present in report
        val candidate = list.firstOrNull { it.isCurrentUser }
        assertNotNull(candidate)
        assertEquals(1420, candidate?.rank)
    }
}
