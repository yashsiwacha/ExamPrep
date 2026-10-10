package com.examprep.core.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Security utilities for credentials hashing, input sanitization,
 * and boundary validation.
 *
 * Security decisions:
 * - PBKDF2-HMAC-SHA256 with 100,000 iterations (OWASP recommended minimum)
 * - 32-byte salt (256-bit) for password hashing
 * - 256-bit derived key length
 * - Constant-time comparison (MessageDigest.isEqual) to prevent timing attacks
 * - HTML/script-injection sanitisation on all user inputs
 */
object SecurityUtils {

    private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val PBKDF2_ITERATIONS = 100_000
    private const val PBKDF2_KEY_LENGTH_BITS = 256
    private const val SALT_LENGTH_BYTES = 32

    private val secureRandom = SecureRandom()

    // ── Salt ────────────────────────────────────────────────────────────────────

    fun generateSalt(lengthBytes: Int = SALT_LENGTH_BYTES): String {
        val salt = ByteArray(lengthBytes)
        secureRandom.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    // ── Password Hashing (PBKDF2-HMAC-SHA256) ───────────────────────────────────

    /**
     * Derive a strong password hash using PBKDF2-HMAC-SHA256.
     * @param password raw plaintext password
     * @param salt     Base64-encoded random salt
     * @return         Base64-encoded derived key
     */
    fun hashPassword(password: String, salt: String): String {
        val saltBytes = Base64.getDecoder().decode(salt)
        val spec: KeySpec = PBEKeySpec(
            password.toCharArray(),
            saltBytes,
            PBKDF2_ITERATIONS,
            PBKDF2_KEY_LENGTH_BITS
        )
        val factory: SecretKeyFactory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
        val derivedKey: ByteArray = factory.generateSecret(spec).encoded
        return Base64.getEncoder().encodeToString(derivedKey)
    }

    /**
     * Verify a password against a stored hash using constant-time comparison.
     * Constant-time prevents timing-based side-channel attacks.
     */
    fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
        return try {
            val computedHash = hashPassword(password, salt)
            MessageDigest.isEqual(
                computedHash.toByteArray(Charsets.UTF_8),
                expectedHash.toByteArray(Charsets.UTF_8)
            )
        } catch (e: Exception) {
            // Never leak exceptions during auth — silently return false
            false
        }
    }

    // ── Input Sanitisation ───────────────────────────────────────────────────────

    /**
     * Sanitize freeform text input: strips null bytes, HTML tags, and script
     * content. Safe for display in UI and storage.
     */
    fun sanitizeInput(input: String, maxLength: Int = 250): String {
        return input
            .replace("\u0000", "")                                               // Strip null bytes
            .replace("(?i)<script[\\s\\S]*?>[\\s\\S]*?<\\/script>".toRegex(), "") // Strip scripts
            .replace("<[^>]*>".toRegex(), "")                                    // Strip HTML tags
            .trim()
            .take(maxLength)
    }

    // ── Validation Helpers ───────────────────────────────────────────────────────

    fun isValidEmail(email: String): Boolean {
        if (email.isBlank() || email.length > 120) return false
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        return emailRegex.matches(email.trim())
    }

    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && name.length in 2..60 &&
            name.all { it.isLetter() || it.isWhitespace() || it == '-' || it == '\'' }
    }

    fun validatePasswordStrength(password: String): PasswordValidationResult {
        if (password.length < 8) {
            return PasswordValidationResult.Weak("Password must be at least 8 characters")
        }
        val hasLetter = password.any { it.isLetter() }
        val hasDigit  = password.any { it.isDigit() }
        if (!hasLetter || !hasDigit) {
            return PasswordValidationResult.Weak("Password must contain letters and numbers")
        }
        return PasswordValidationResult.Valid
    }
}

sealed interface PasswordValidationResult {
    object Valid : PasswordValidationResult
    data class Weak(val reason: String) : PasswordValidationResult
}
