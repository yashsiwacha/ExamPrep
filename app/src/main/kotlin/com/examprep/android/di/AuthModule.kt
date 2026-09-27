package com.examprep.android.di

import com.examprep.android.feature.auth.AuthStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module ensuring that AuthStore is a single unified singleton
 * across both ViewModel dependency injection and static composable calls.
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthStore(): AuthStore = AuthStore.get()
}
