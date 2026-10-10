package com.examprep.data.firebase

import com.examprep.domain.model.UserProfile
import com.examprep.domain.repository.CloudSyncProgressSummary
import com.examprep.domain.repository.CloudSyncRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase Firestore implementation for student progress and profile sync.
 *
 * Cloud Architecture:
 * - /users/{userId} -> Student profile metadata
 * - /users/{userId}/progress/summary -> Progress metrics, streak, accuracy, mastery
 *
 * Resilience:
 * - Leverages Firestore's offline disk cache automatically
 * - Safe exception handling so app remains 100% functional even when offline
 */
@Singleton
class FirebaseProgressSyncRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : CloudSyncRepository {

    override suspend fun syncProgressToCloud(summary: CloudSyncProgressSummary): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val currentUserId = summary.userId.ifBlank {
                    auth.currentUser?.uid ?: "anonymous_student"
                }

                val progressData = hashMapOf(
                    "userId" to currentUserId,
                    "targetExamId" to summary.targetExamId,
                    "targetYear" to summary.targetYear,
                    "dailyStreak" to summary.dailyStreak,
                    "totalStudyMinutes" to summary.totalStudyMinutes,
                    "questionsAttempted" to summary.questionsAttempted,
                    "questionsCorrect" to summary.questionsCorrect,
                    "overallAccuracy" to summary.overallAccuracy,
                    "masteredTopicsCount" to summary.masteredTopicsCount,
                    "nationalRankEstimate" to summary.nationalRankEstimate,
                    "lastSyncEpochMs" to summary.lastSyncEpochMs,
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )

                firestore.collection("users")
                    .document(currentUserId)
                    .collection("progress")
                    .document("summary")
                    .set(progressData, SetOptions.merge())
                    .await()

                Timber.d("Progress successfully synced to Firestore for user: $currentUserId")
                Result.success(Unit)
            } catch (e: Exception) {
                Timber.w(e, "Cloud progress sync failed (offline or network error). Data remains safe in local Room DB.")
                Result.failure(e)
            }
        }

    override suspend fun fetchProgressFromCloud(userId: String): Result<CloudSyncProgressSummary?> =
        withContext(Dispatchers.IO) {
            try {
                val targetUserId = userId.ifBlank {
                    auth.currentUser?.uid ?: return@withContext Result.success(null)
                }

                val doc = firestore.collection("users")
                    .document(targetUserId)
                    .collection("progress")
                    .document("summary")
                    .get()
                    .await()

                if (!doc.exists()) {
                    return@withContext Result.success(null)
                }

                val summary = CloudSyncProgressSummary(
                    userId = doc.getString("userId") ?: targetUserId,
                    targetExamId = doc.getString("targetExamId") ?: "",
                    targetYear = (doc.getLong("targetYear") ?: 2026L).toInt(),
                    dailyStreak = (doc.getLong("dailyStreak") ?: 0L).toInt(),
                    totalStudyMinutes = (doc.getLong("totalStudyMinutes") ?: 0L).toInt(),
                    questionsAttempted = (doc.getLong("questionsAttempted") ?: 0L).toInt(),
                    questionsCorrect = (doc.getLong("questionsCorrect") ?: 0L).toInt(),
                    overallAccuracy = (doc.getDouble("overallAccuracy") ?: 0.0).toFloat(),
                    masteredTopicsCount = (doc.getLong("masteredTopicsCount") ?: 0L).toInt(),
                    nationalRankEstimate = doc.getString("nationalRankEstimate") ?: "Top 15.0%",
                    lastSyncEpochMs = doc.getLong("lastSyncEpochMs") ?: System.currentTimeMillis()
                )

                Result.success(summary)
            } catch (e: Exception) {
                Timber.w(e, "Failed to fetch cloud progress summary for user $userId")
                Result.failure(e)
            }
        }

    override suspend fun syncUserProfile(profile: UserProfile): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val currentUserId = profile.id.ifBlank {
                    auth.currentUser?.uid ?: "anonymous_student"
                }

                val profileData = hashMapOf(
                    "userId" to currentUserId,
                    "name" to (profile.name ?: "Student"),
                    "selectedExamId" to (profile.selectedExamId ?: "jee_main"),
                    "dailyStudyHours" to profile.dailyStudyHours,
                    "studyDaysPerWeek" to profile.studyDaysPerWeek,
                    "preferredSessionMinutes" to profile.preferredSessionMinutes,
                    "onboardingCompleted" to profile.onboardingCompleted,
                    "diagnosticCompleted" to profile.diagnosticCompleted,
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )

                firestore.collection("users")
                    .document(currentUserId)
                    .set(profileData, SetOptions.merge())
                    .await()

                Timber.d("User profile synced to Firestore: $currentUserId")
                Result.success(Unit)
            } catch (e: Exception) {
                Timber.w(e, "Failed to sync user profile to Firestore: ${e.message}")
                Result.failure(e)
            }
        }
}
