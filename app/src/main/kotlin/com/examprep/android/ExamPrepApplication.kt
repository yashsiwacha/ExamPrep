package com.examprep.android

import android.app.Application
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
    }

    private fun initLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        // SECURITY: No logging in release builds — prevents leaking sensitive study data
    }
}
