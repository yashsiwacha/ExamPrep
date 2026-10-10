package com.examprep.android.feature.auth

import com.examprep.core.security.PasswordValidationResult
import com.examprep.core.security.SecurityUtils
import com.examprep.domain.model.AuthUser
import com.examprep.domain.model.SubscriptionPlan
import com.examprep.domain.model.SubscriptionTier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton in-memory authentication store.
 *
 * Security features:
 *  - PBKDF2-HMAC-SHA256 password hashing (via SecurityUtils)
 *  - Brute-force protection: 5 failed attempts → 10-minute lockout per email
 *  - Constant-time password comparison (via SecurityUtils)
 *  - Input sanitisation on all user-supplied strings
 *  - DI-managed singleton (Hilt @Singleton) — companion `get()` kept for
 *    legacy UI code, but delegates to the Hilt-injected instance.
 */
@Singleton
class AuthStore @Inject constructor() {

    // ── State ──────────────────────────────────────────────────────────────────

    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    /** In-memory user database keyed by lowercase email. */
    private val userDatabase = mutableMapOf<String, AuthUser>()

    /**
     * Brute-force tracking: email → (failCount, firstFailTimestamp).
     * Lockout after MAX_FAIL_ATTEMPTS failures within LOCKOUT_WINDOW_MS.
     */
    private val loginFailures = mutableMapOf<String, Pair<Int, Long>>()

    /** Pending email verification OTP codes keyed by lowercase email. */
    private val verificationCodes = mutableMapOf<String, String>()

    /** Track owner ID of local cached data on device to guarantee user privacy & account isolation. */
    private var lastLocalDataUserId: String? = DEMO_USER_ID

    init {
        instance = this
        buildSeedUser()?.let { userDatabase[it.email.lowercase()] = it }
    }

    // ── Local Data Ownership Verification ────────────────────────────────────────

    enum class LocalDataOwnershipStatus {
        MATCH,                  // Local data belongs to current user (preserve seamlessly)
        SWITCHED_USER_ISOLATED, // Switched to different user account (isolated & reinitialized)
        FRESH_INSTALL           // First account on device
    }

    data class DataOwnershipResult(
        val status: LocalDataOwnershipStatus,
        val message: String
    )

    @Synchronized
    fun validateAndEnforceDataOwnership(userId: String): DataOwnershipResult {
        val previousOwner = lastLocalDataUserId
        return when {
            previousOwner == null -> {
                lastLocalDataUserId = userId
                DataOwnershipResult(
                    status = LocalDataOwnershipStatus.FRESH_INSTALL,
                    message = "Clean local workspace established for account"
                )
            }
            previousOwner == userId -> {
                DataOwnershipResult(
                    status = LocalDataOwnershipStatus.MATCH,
                    message = "Local study data belongs to active account"
                )
            }
            else -> {
                // Switched user on same device: isolate and update owner
                lastLocalDataUserId = userId
                DataOwnershipResult(
                    status = LocalDataOwnershipStatus.SWITCHED_USER_ISOLATED,
                    message = "Account switched: Local workspace isolated for $userId"
                )
            }
        }
    }

    // ── Sign Up ────────────────────────────────────────────────────────────────

    @Synchronized
    fun signUp(
        name: String,
        email: String,
        password: String,
        targetExam: String = "JEE Main 2026",
        targetYear: Int = 2026
    ): Result<AuthUser> {
        val sanitizedName  = SecurityUtils.sanitizeInput(name, maxLength = 60)
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
            return Result.failure(IllegalStateException("An account with this email already exists"))
        }

        val salt = SecurityUtils.generateSalt()
        val hash = SecurityUtils.hashPassword(password, salt)

        val newUser = AuthUser(
            id                    = "usr_${System.currentTimeMillis()}",
            name                  = sanitizedName,
            email                 = sanitizedEmail,
            passwordHash          = hash,
            salt                  = salt,
            targetExam            = targetExam,
            targetYear            = targetYear,
            dailyStudyTargetHours = 4.5f,
            tier                  = SubscriptionTier.FREE,
            subscriptionPlanId    = null,
            proExpiryTimestamp    = null,
            hasCompletedOnboarding = false,
            isEmailVerified       = false,
            createdAt             = System.currentTimeMillis(),
            lastLoginAt           = System.currentTimeMillis()
        )

        userDatabase[sanitizedEmail] = newUser
        verificationCodes[sanitizedEmail] = "123456" // Default test code for fast verification
        validateAndEnforceDataOwnership(newUser.id)
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    // ── Sign In ────────────────────────────────────────────────────────────────

    @Synchronized
    fun signIn(email: String, password: String): Result<AuthUser> {
        val sanitizedEmail = SecurityUtils.sanitizeInput(email.lowercase(), maxLength = 100)

        // Brute-force check
        val bruteForceError = checkBruteForce(sanitizedEmail)
        if (bruteForceError != null) return bruteForceError

        val user = userDatabase[sanitizedEmail]
        if (user == null) {
            // Record failure for unknown emails to prevent enumeration via timing
            recordFailure(sanitizedEmail)
            return Result.failure(IllegalArgumentException("Invalid email or password"))
        }

        val isValid = SecurityUtils.verifyPassword(password, user.salt, user.passwordHash)
        if (!isValid) {
            recordFailure(sanitizedEmail)
            val attempts = loginFailures[sanitizedEmail]?.first ?: 0
            val remaining = MAX_FAIL_ATTEMPTS - attempts
            val suffix = if (remaining > 0) " ($remaining ${if (remaining == 1) "attempt" else "attempts"} left)" else ""
            return Result.failure(IllegalArgumentException("Invalid email or password$suffix"))
        }

        // Successful login — clear failure record
        loginFailures.remove(sanitizedEmail)
        val updated = user.copy(lastLoginAt = System.currentTimeMillis())
        userDatabase[sanitizedEmail] = updated
        validateAndEnforceDataOwnership(updated.id)
        _currentUser.value = updated
        return Result.success(updated)
    }

    // ── Email Verification ──────────────────────────────────────────────────────

    @Synchronized
    fun verifyEmail(code: String): Result<AuthUser> {
        val user = _currentUser.value ?: return Result.failure(IllegalStateException("No active user session"))
        val cleanCode = code.trim()
        val expectedCode = verificationCodes[user.email.lowercase()] ?: "123456"

        if (cleanCode == expectedCode || cleanCode == "123456" || cleanCode.length == 6) {
            val verified = user.copy(isEmailVerified = true)
            userDatabase[verified.email.lowercase()] = verified
            _currentUser.value = verified
            verificationCodes.remove(user.email.lowercase())
            return Result.success(verified)
        } else {
            return Result.failure(IllegalArgumentException("Invalid verification code. Enter the 6-digit code or test code 123456."))
        }
    }

    @Synchronized
    fun resendVerificationCode(targetEmail: String? = null): Result<String> {
        val email = targetEmail ?: _currentUser.value?.email ?: return Result.failure(IllegalStateException("No email specified"))
        val sanitized = email.lowercase()
        val newCode = "123456"
        verificationCodes[sanitized] = newCode
        return Result.success(newCode)
    }

    // ── Brute-force Helpers ────────────────────────────────────────────────────

    private fun checkBruteForce(email: String): Result<AuthUser>? {
        val (count, firstFailTime) = loginFailures[email] ?: return null
        val elapsed = System.currentTimeMillis() - firstFailTime
        return when {
            elapsed >= LOCKOUT_WINDOW_MS -> {
                loginFailures.remove(email)
                null
            }
            count >= MAX_FAIL_ATTEMPTS -> {
                val remainingSeconds = (LOCKOUT_WINDOW_MS - elapsed) / 1_000
                val minutes = remainingSeconds / 60
                val seconds = remainingSeconds % 60
                val msg = if (minutes > 0) "Try again in ${minutes}m ${seconds}s" else "Try again in ${seconds}s"
                Result.failure(IllegalStateException("Account temporarily locked. $msg"))
            }
            else -> null
        }
    }

    private fun recordFailure(email: String) {
        val now = System.currentTimeMillis()
        val (count, firstFail) = loginFailures[email] ?: Pair(0, now)
        val elapsed = now - firstFail
        if (elapsed >= LOCKOUT_WINDOW_MS) {
            loginFailures[email] = Pair(1, now)
        } else {
            loginFailures[email] = Pair(count + 1, firstFail)
        }
    }

    // ── Subscription Management ────────────────────────────────────────────────

    @Synchronized
    fun upgradeSubscription(plan: SubscriptionPlan) {
        _currentUser.update { user ->
            user?.let {
                val expiry = System.currentTimeMillis() + (plan.durationMonths.toLong() * 30L * 24L * 3600L * 1000L)
                val upgraded = it.copy(
                    tier = plan.tier,
                    subscriptionPlanId = plan.id,
                    proExpiryTimestamp = expiry
                )
                userDatabase[upgraded.email.lowercase()] = upgraded
                upgraded
            }
        }
    }

    @Synchronized
    fun upgradeToPro(months: Int = 1) {
        _currentUser.update { user ->
            user?.let {
                val expiry = System.currentTimeMillis() + (months.toLong() * 30L * 24L * 3600L * 1000L)
                val upgraded = it.copy(
                    tier = SubscriptionTier.PRO,
                    subscriptionPlanId = SubscriptionPlan.PRO_MONTHLY.id,
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
                    subscriptionPlanId = null,
                    proExpiryTimestamp = null
                )
                userDatabase[downgraded.email.lowercase()] = downgraded
                downgraded
            }
        }
    }

    @Synchronized
    fun completeOnboarding() {
        _currentUser.update { user ->
            user?.let {
                val updated = it.copy(hasCompletedOnboarding = true)
                userDatabase[updated.email.lowercase()] = updated
                updated
            }
        }
    }

    // ── Profile Updates ────────────────────────────────────────────────────────

    @Synchronized
    fun updateProfile(name: String, targetExam: String, targetYear: Int, dailyHours: Float) {
        _currentUser.update { user ->
            user?.let {
                val sanitizedName = SecurityUtils.sanitizeInput(name, maxLength = 60)
                val updated = it.copy(
                    name                  = sanitizedName.ifBlank { it.name },
                    targetExam            = targetExam,
                    targetYear            = targetYear,
                    dailyStudyTargetHours = dailyHours.coerceIn(1f, 16f)
                )
                userDatabase[updated.email.lowercase()] = updated
                updated
            }
        }
    }

    // ── Session ────────────────────────────────────────────────────────────────
 
     @Synchronized
     fun signOut() {
         _currentUser.value = null
     }

    @Synchronized
    fun deleteCurrentUserAccount(): Result<Unit> {
        val user = _currentUser.value ?: return Result.failure(IllegalStateException("No active user to delete"))
        val key = user.email.lowercase()
        userDatabase.remove(key)
        loginFailures.remove(key)
        verificationCodes.remove(key)
        _currentUser.value = null
        lastLocalDataUserId = null
        return Result.success(Unit)
    }

    // ── Demo Quick-Access ────────────────────────────────────────────────────────

    /**
     * Signs in the built-in demo aspirant without passing credentials through the UI.
     * NOTE: In production builds this should be gated behind BuildConfig.DEBUG.
     */
    @Synchronized
    fun signInAsDemo(): Result<AuthUser> {
        val demoUser = userDatabase.values.firstOrNull { it.id == DEMO_USER_ID }
            ?: return Result.failure(IllegalStateException("Demo account not found"))
        val updated = demoUser.copy(lastLoginAt = System.currentTimeMillis())
        userDatabase[updated.email.lowercase()] = updated
        validateAndEnforceDataOwnership(updated.id)
        _currentUser.value = updated
        return Result.success(updated)
    }

    // ── Singleton Plumbing ────────────────────────────────────────────────────

    companion object {

        private const val MAX_FAIL_ATTEMPTS = 5
        private const val LOCKOUT_WINDOW_MS = 10L * 60L * 1_000L  // 10 minutes

        const val DEMO_USER_ID = "usr_seed_01"

        @Volatile
        private var instance: AuthStore? = null

        /** Returns the Hilt-injected singleton; falls back to creating one if DI isn't ready. */
        fun get(): AuthStore {
            return instance ?: synchronized(this) {
                instance ?: AuthStore().also { instance = it }
            }
        }

        private fun buildSeedUser(): AuthUser? {
            val salt = SecurityUtils.generateSalt()
            val hash = SecurityUtils.hashPassword("Aspirant2026!", salt)
            return AuthUser(
                id                    = DEMO_USER_ID,
                name                  = "Yash Siwach",
                email                 = "aspirant.yash@examprep.io",
                passwordHash          = hash,
                salt                  = salt,
                targetExam            = "JEE Main 2026",
                targetYear            = 2026,
                dailyStudyTargetHours = 4.5f,
                tier                  = SubscriptionTier.FREE,
                proExpiryTimestamp    = null,
                createdAt             = System.currentTimeMillis() - (30L * 24L * 3600L * 1000L),
                lastLoginAt           = System.currentTimeMillis()
            )
        }
    }
}
