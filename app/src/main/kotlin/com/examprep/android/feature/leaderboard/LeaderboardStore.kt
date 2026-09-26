package com.examprep.android.feature.leaderboard

import com.examprep.domain.model.LeaderboardStudent
import com.examprep.domain.model.PeerBenchmarkReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardStore @Inject constructor() {

    private val _report = MutableStateFlow(defaultReport())
    val report: StateFlow<PeerBenchmarkReport> = _report.asStateFlow()

    companion object {
        private var instance: LeaderboardStore? = null
        fun get(): LeaderboardStore {
            return instance ?: LeaderboardStore().also { instance = it }
        }

        private fun defaultReport(): PeerBenchmarkReport = PeerBenchmarkReport(
            examTitle = "All-India JEE Main Grand Simulator #3",
            totalParticipants = 48290,
            userRank = 1420,
            userPercentile = 97.06f,
            userScore = 224,
            topperScore = 296,
            averageScore = 138,
            accuracyComparison = 81.5f,
            leaderboard = listOf(
                LeaderboardStudent(1, "Aarav Sharma", "👑 AIR 1", 296, 98.2f, 42, false, "Kota, RJ"),
                LeaderboardStudent(2, "Diya Patel", "🥈 AIR 2", 292, 97.5f, 38, false, "Ahmedabad, GJ"),
                LeaderboardStudent(3, "Rohan Verma", "🥉 AIR 3", 288, 96.8f, 55, false, "Hyderabad, TS"),
                LeaderboardStudent(4, "Ananya Iyer", "⭐ Top 10", 284, 95.9f, 29, false, "Bengaluru, KA"),
                LeaderboardStudent(5, "Kabir Mehta", "⭐ Top 10", 280, 95.0f, 31, false, "Mumbai, MH"),
                LeaderboardStudent(6, "Sneha Reddy", "⭐ Top 10", 276, 94.2f, 19, false, "Visakhapatnam, AP"),
                LeaderboardStudent(7, "Siddharth Das", "⭐ Top 10", 272, 93.8f, 24, false, "Kolkata, WB"),
                LeaderboardStudent(8, "Pooja Choudhury", "⭐ Top 10", 268, 93.1f, 18, false, "Jaipur, RJ"),
                LeaderboardStudent(1420, "You (Candidate)", "🔥 97.1%ile", 224, 81.5f, 14, true, "New Delhi, DL")
            )
        )
    }
}
