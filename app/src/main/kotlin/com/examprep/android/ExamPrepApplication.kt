package com.examprep.android

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * ExamPrep Application class.
 *
 * Entry point for Hilt dependency injection graph.
 * Initializes application-level dependencies and logging.
 *
 * Architecture: This class must remain minimal.
 * Business logic belongs in domain/use-case layers.
 */
@HiltAndroidApp
class ExamPrepApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initLogging()
        initNotifications()
        initAdMob()
    }

    private fun initNotifications() {
        com.examprep.android.notification.NotificationHelper.createNotificationChannels(this)
        com.examprep.android.notification.StudyReminderWorker.scheduleDailyReminder(this)
    }

    private fun initLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        // SECURITY: No logging in release builds — prevents leaking sensitive study data
    }

    /**
     * Initialize Google Mobile Ads SDK.
     *
     * Runs on a background thread to avoid blocking the main thread startup.
     * The callback is called on the main thread once initialization is complete.
     *
     * NOTE: Replace the test App ID in AndroidManifest.xml with your real
     * AdMob App ID before publishing to Google Play.
     */
    private fun initAdMob() {
        Thread {
            MobileAds.initialize(this) { initializationStatus ->
                Timber.d("AdMob initialized: $initializationStatus")
            }
        }.start()
    }
}

