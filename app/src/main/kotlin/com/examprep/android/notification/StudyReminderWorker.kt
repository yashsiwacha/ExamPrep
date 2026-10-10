package com.examprep.android.notification

import android.content.Context
import androidx.work.*
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import com.examprep.domain.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class StudyReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface StudyReminderWorkerEntryPoint {
        fun userProfileRepository(): UserProfileRepository
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                StudyReminderWorkerEntryPoint::class.java
            )
            val profile = entryPoint.userProfileRepository().getUserProfileOnce()

            // If onboarding is completed, send a gentle study reminder
            if (profile != null && profile.onboardingCompleted) {
                val studentName = profile.name ?: "Aspirant"
                val examDaysRemaining = profile.examDate?.let { date ->
                    val diff = (date - System.currentTimeMillis()) / (24 * 60 * 60 * 1000L)
                    if (diff > 0) " • $diff days to exam" else ""
                } ?: ""

                NotificationHelper.showStudyReminderNotification(
                    context = applicationContext,
                    title = "Stay on Track, $studentName!",
                    message = "Today's targeted preparation tasks are waiting$examDaysRemaining. Keep your momentum going!"
                )
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "examprep_daily_study_reminder"

        fun scheduleDailyReminder(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<StudyReminderWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(12, TimeUnit.HOURS) // Deliver first reminder in daytime
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        fun cancelDailyReminder(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
