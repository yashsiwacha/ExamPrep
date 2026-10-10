package com.examprep.data.firebase

import com.examprep.domain.repository.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

import com.examprep.domain.model.AuthUser
import com.examprep.domain.model.SubscriptionTier
import com.google.firebase.auth.userProfileChangeRequest

/**
 * Firebase Authentication implementation for email verification,
 * user registration, authentication, live status reload, and password reset operations.
 */
@Singleton
class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : FirebaseAuthRepository {

    override suspend fun signUpWithFirebase(
        name: String,
        email: String,
        password: String
    ): Result<AuthUser> = withContext(Dispatchers.IO) {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = authResult.user ?: return@withContext Result.failure(
                IllegalStateException("Firebase user creation returned null")
            )

            // Update display name
            val profileUpdates = userProfileChangeRequest {
                displayName = name.trim()
            }
            firebaseUser.updateProfile(profileUpdates).await()

            // Dispatch verification email immediately
            try {
                firebaseUser.sendEmailVerification().await()
                Timber.i("Firebase verification email dispatched to ${firebaseUser.email}")
            } catch (e: Exception) {
                Timber.w(e, "Firebase sendEmailVerification warning: ${e.message}")
            }

            val user = AuthUser(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: email.trim(),
                name = name.trim(),
                passwordHash = "FIREBASE_MANAGED",
                salt = "FIREBASE_MANAGED",
                tier = SubscriptionTier.FREE,
                targetExam = "JEE Main 2026",
                targetYear = 2026,
                hasCompletedOnboarding = false,
                isEmailVerified = firebaseUser.isEmailVerified,
                createdAt = System.currentTimeMillis(),
                lastLoginAt = System.currentTimeMillis()
            )
            Result.success(user)
        } catch (e: Exception) {
            Timber.e(e, "Firebase signUpWithFirebase failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun signInWithFirebase(
        email: String,
        password: String
    ): Result<AuthUser> = withContext(Dispatchers.IO) {
        try {
            val authResult = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = authResult.user ?: return@withContext Result.failure(
                IllegalStateException("Firebase sign in returned null user")
            )

            // Reload user to get freshest verification status
            try {
                firebaseUser.reload().await()
            } catch (e: Exception) {
                Timber.w("Could not reload Firebase user: ${e.message}")
            }

            val user = AuthUser(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: email.trim(),
                name = firebaseUser.displayName ?: "Aspirant",
                passwordHash = "FIREBASE_MANAGED",
                salt = "FIREBASE_MANAGED",
                tier = SubscriptionTier.FREE,
                targetExam = "JEE Main 2026",
                targetYear = 2026,
                hasCompletedOnboarding = true,
                isEmailVerified = firebaseUser.isEmailVerified,
                createdAt = firebaseUser.metadata?.creationTimestamp ?: System.currentTimeMillis(),
                lastLoginAt = System.currentTimeMillis()
            )
            Result.success(user)
        } catch (e: Exception) {
            Timber.e(e, "Firebase signInWithFirebase failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun sendEmailVerification(): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val user = auth.currentUser
                if (user == null) {
                    Timber.w("Cannot send verification email: No active user session")
                    return@withContext Result.failure(IllegalStateException("No signed-in user found"))
                }
                user.sendEmailVerification().await()
                Timber.d("Verification email successfully dispatched to ${user.email}")
                Result.success(Unit)
            } catch (e: Exception) {
                Timber.w(e, "Failed to send email verification: ${e.message}")
                Result.failure(e)
            }
        }

    override suspend fun checkEmailVerified(): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val user = auth.currentUser
                if (user == null) {
                    return@withContext Result.success(false)
                }
                user.reload().await()
                val isVerified = user.isEmailVerified
                Timber.d("User email verification status: $isVerified for ${user.email}")
                Result.success(isVerified)
            } catch (e: Exception) {
                Timber.w(e, "Failed to reload user verification status: ${e.message}")
                Result.failure(e)
            }
        }

    override suspend fun sendPasswordReset(email: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                if (email.isBlank()) {
                    return@withContext Result.failure(IllegalArgumentException("Email cannot be blank"))
                }
                auth.sendPasswordResetEmail(email.trim()).await()
                Timber.d("Password reset email sent to $email")
                Result.success(Unit)
            } catch (e: Exception) {
                Timber.w(e, "Failed to send password reset email: ${e.message}")
                Result.failure(e)
            }
        }

    override suspend fun signOut(): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                auth.signOut()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun deleteAccount(): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val user = auth.currentUser
                if (user != null) {
                    user.delete().await()
                    Timber.i("Firebase user account deleted successfully")
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Timber.w(e, "Firebase account deletion failed: ${e.message}")
                Result.failure(e)
            }
        }

    override fun isEmailVerified(): Boolean {
        return auth.currentUser?.isEmailVerified ?: false
    }

    override fun getCurrentFirebaseUserEmail(): String? {
        return auth.currentUser?.email
    }
}
