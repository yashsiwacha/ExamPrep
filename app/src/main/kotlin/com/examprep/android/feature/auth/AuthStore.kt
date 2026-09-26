package com.examprep.android.feature.auth

import com.examprep.core.security.PasswordValidationResult
import com.examprep.core.security.SecurityUtils
import com.examprep.domain.model.AuthUser
import com.examprep.domain.model.SubscriptionTier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStore @Inject constructor() {

    private val _currentUser = MutableStateFlow<AuthUser?>(defaultSeedUser())
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    private val userDatabase = mutableMapOf<String, AuthUser>()

    init {
        defaultSeedUser()?.let { userDatabase[it.email.lowercase()] = it }
    }

    @Synchronized
    fun signUp(
        name: String,
        email: String,
        password: String,
        targetExam: String = "JEE Main 2026",
        targetYear: Int = 2026
    ): Result<AuthUser> {
        val sanitizedName = SecurityUtils.sanitizeInput(name, maxLength = 60)
        val sanitizedEmail = SecurityUtils.sanitizeInput(email.lowercase(), maxLength = 100)

        if (sanitizedName.isBlank()) {
            return Result.failure(IllegalArgumentException("Name cannot be blank"))
        }
        if (!SecurityUtils.isValidEmail(sanitizedEmail)) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }

        val passCheck = SecurityUtils.validatePasswordStrength(password)
        if (passCheck is PasswordValidationResult.Weak) {
            return Result.failure(IllegalArgumentException(passCheck.reason))
        }

        if (userDatabase.containsKey(sanitizedEmail)) {
            return Result.failure(IllegalStateException("Account with this email already exists"))
        }

        val salt = SecurityUtils.generateSalt()
        val hash = SecurityUtils.hashPassword(password, salt)

        val newUser = AuthUser(
            id = "usr_${System.currentTimeMillis()}",
            name = sanitizedName,
            email = sanitizedEmail,
            passwordHash = hash,
            salt = salt,
            targetExam = targetExam,
            targetYear = targetYear,
            dailyStudyTargetHours = 4.5f,
            tier = SubscriptionTier.FREE,
            proExpiryTimestamp = null,
            createdAt = System.currentTimeMillis(),
            lastLoginAt = System.currentTimeMillis()
        )

        userDatabase[sanitizedEmail] = newUser
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    @Synchronized
    fun signIn(email: String, password: String): Result<AuthUser> {
        val sanitizedEmail = SecurityUtils.sanitizeInput(email.lowercase(), maxLength = 100)
        val user = userDatabase[sanitizedEmail]
            ?: return Result.failure(IllegalArgumentException("Invalid email or password"))

        val isValid = SecurityUtils.verifyPassword(password, user.salt, user.passwordHash)
        if (!isValid) {
            return Result.failure(IllegalArgumentException("Invalid email or password"))
        }

        val updated = user.copy(lastLoginAt = System.currentTimeMillis())
        userDatabase[sanitizedEmail] = updated
        _currentUser.value = updated
        return Result.success(updated)
    }

    @Synchronized
    fun upgradeToPro(months: Int = 12) {
        _currentUser.update { user ->
            user?.let {
                val expiry = System.currentTimeMillis() + (months.toLong() * 30L * 24L * 3600L * 1000L)
                val upgraded = it.copy(
                    tier = SubscriptionTier.PRO,
                    proExpiryTimestamp = expiry
                )
                userDatabase[upgraded.email.lowercase()] = upgraded
                upgraded
            }
        }
    }

    @Synchronized
    fun downgradeToFree() {
        _currentUser.update { user ->
            user?.let {
                val downgraded = it.copy(
                    tier = SubscriptionTier.FREE,
                    proExpiryTimestamp = null
                )
                userDatabase[downgraded.email.lowercase()] = downgraded
                downgraded
            }
        }
    }

    @Synchronized
    fun updateProfile(name: String, targetExam: String, targetYear: Int, dailyHours: Float) {
        _currentUser.update { user ->
            user?.let {
                val sanitizedName = SecurityUtils.sanitizeInput(name, maxLength = 60)
                val updated = it.copy(
                    name = sanitizedName.ifBlank { it.name },
                    targetExam = targetExam,
                    targetYear = targetYear,
                    dailyStudyTargetHours = dailyHours.coerceIn(1f, 16f)
                )
                userDatabase[updated.email.lowercase()] = updated
                updated
            }
        }
    }

    @Synchronized
    fun signOut() {
        _currentUser.value = null
    }

    companion object {
        private var instance: AuthStore? = null
        fun get(): AuthStore {
            return instance ?: AuthStore().also { instance = it }
        }

        private fun defaultSeedUser(): AuthUser? {
            val salt = SecurityUtils.generateSalt()
            val hash = SecurityUtils.hashPassword("Aspirant2026!", salt)
            return AuthUser(
                id = "usr_seed_01",
                name = "Yash Siwach",
                email = "aspirant.yash@examprep.io",
                passwordHash = hash,
                salt = salt,
                targetExam = "JEE Main 2026",
                targetYear = 2026,
                dailyStudyTargetHours = 4.5f,
                tier = SubscriptionTier.FREE,
                proExpiryTimestamp = null,
                createdAt = System.currentTimeMillis() - (30L * 24L * 3600L * 1000L),
                lastLoginAt = System.currentTimeMillis()
            )
        }
    }
}
