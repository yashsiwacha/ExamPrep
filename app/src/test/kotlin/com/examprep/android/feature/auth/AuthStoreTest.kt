package com.examprep.android.feature.auth

import com.examprep.core.security.SecurityUtils
import com.examprep.domain.model.SubscriptionTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthStoreTest {

    private lateinit var store: AuthStore

    @Before
    fun setUp() {
        store = AuthStore()
    }

    @Test
    fun testDefaultSeedUserLoaded() {
        val user = store.currentUser.value
        assertNotNull(user)
        assertEquals("aspirant.yash@examprep.io", user?.email)
        assertEquals(SubscriptionTier.FREE, user?.tier)
        assertFalse(user?.isPro ?: true)
    }

    @Test
    fun testSignUpSuccessWithSaltedHash() {
        val result = store.signUp(
            name = "Aarav Sharma",
            email = "aarav.sharma@gmail.com",
            password = "StrongPassword2026!",
            targetExam = "JEE Main 2026",
            targetYear = 2026
        )

        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("Aarav Sharma", user?.name)
        assertEquals("aarav.sharma@gmail.com", user?.email)
        assertNotEquals("StrongPassword2026!", user?.passwordHash)
        assertTrue(user?.salt?.isNotEmpty() == true)

        // Verify cryptographic verification matches
        assertTrue(SecurityUtils.verifyPassword("StrongPassword2026!", user!!.salt, user.passwordHash))
        assertFalse(SecurityUtils.verifyPassword("WrongPassword", user.salt, user.passwordHash))
    }

    @Test
    fun testSignUpDuplicateEmailFails() {
        val email = "duplicate.test@examprep.io"
        val first = store.signUp("User One", email, "Pass12345")
        assertTrue(first.isSuccess)

        val second = store.signUp("User Two", email, "Pass12345")
        assertTrue(second.isFailure)
        assertTrue(second.exceptionOrNull()?.message?.contains("already exists") == true)
    }

    @Test
    fun testWeakPasswordRejected() {
        val shortPass = store.signUp("User Short", "short@test.com", "abc1")
        assertTrue(shortPass.isFailure)

        val noDigitPass = store.signUp("User NoDigit", "nodigit@test.com", "alllettersonly")
        assertTrue(noDigitPass.isFailure)
    }

    @Test
    fun testSignInValidAndInvalidCredentials() {
        val email = "signin.test@examprep.io"
        store.signUp("Sign In User", email, "SecretPass123")

        val validSignIn = store.signIn(email, "SecretPass123")
        assertTrue(validSignIn.isSuccess)
        assertEquals("Sign In User", validSignIn.getOrNull()?.name)

        val invalidSignIn = store.signIn(email, "WrongSecret")
        assertTrue(invalidSignIn.isFailure)
    }

    @Test
    fun testProUpgradeAndDowngradeLifecycle() {
        val initialUser = store.currentUser.value
        assertNotNull(initialUser)
        assertFalse(initialUser!!.isPro)

        // Upgrade to PRO
        store.upgradeToPro(months = 12)
        var updated = store.currentUser.value
        assertEquals(SubscriptionTier.PRO, updated?.tier)
        assertTrue(updated?.isPro == true)
        assertTrue((updated?.proExpiryTimestamp ?: 0L) > System.currentTimeMillis())

        // Downgrade back to FREE
        store.downgradeToFree()
        updated = store.currentUser.value
        assertEquals(SubscriptionTier.FREE, updated?.tier)
        assertFalse(updated?.isPro == true)
    }

    @Test
    fun testSanitizationRemovesInjectionVectors() {
        val maliciousName = "<script>alert('xss')</script>Rahul \u0000Kumar"
        val result = store.signUp(maliciousName, "rahul.sec@test.com", "SecurePass2026")
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertEquals("Rahul Kumar", user?.name)
        assertFalse(user?.name?.contains("<script>") ?: true)
        assertFalse(user?.name?.contains("\u0000") ?: true)
    }

    @Test
    fun testOnboardingProfileUpdate() {
        store.signUp("Priya Patel", "priya@test.com", "PriyaPass2026!")
        store.updateProfile(
            name = "Priya Patel",
            targetExam = "NEET UG 2026",
            targetYear = 2026,
            dailyHours = 6.5f
        )
        val user = store.currentUser.value
        assertNotNull(user)
        assertEquals("NEET UG 2026", user?.targetExam)
        assertEquals(2026, user?.targetYear)
        assertEquals(6.5f, user?.dailyStudyTargetHours ?: 0f, 0.01f)
    }
}
