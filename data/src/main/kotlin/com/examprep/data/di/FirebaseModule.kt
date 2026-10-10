package com.examprep.data.di

import com.examprep.data.firebase.FirebaseProgressSyncRepositoryImpl
import com.examprep.domain.repository.CloudSyncRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.PersistentCacheSettings
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing Firebase Authentication and Cloud Firestore singletons.
 *
 * Configured with persistent offline caching enabled so reads and writes
 * function even when disconnected from the internet.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseProviderModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(
                PersistentCacheSettings.newBuilder()
                    .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                    .build()
            )
            .build()
        firestore.firestoreSettings = settings
        return firestore
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseBindingModule {

    @Binds
    @Singleton
    abstract fun bindCloudSyncRepository(
        impl: FirebaseProgressSyncRepositoryImpl
    ): CloudSyncRepository

    @Binds
    @Singleton
    abstract fun bindFirebaseAuthRepository(
        impl: com.examprep.data.firebase.FirebaseAuthRepositoryImpl
    ): com.examprep.domain.repository.FirebaseAuthRepository
}
