package com.examprep.core.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

/**
 * Security utilities for credentials hashing, input sanitization,
 * and boundary validation.
 */
object SecurityUtils {

    private val secureRandom = SecureRandom()

    fun generateSalt(lengthBytes: Int = 16): String {
        val salt = ByteArray(lengthBytes)
        secureRandom.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    fun hashPassword(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt.toByteArray(Charsets.UTF_8))
        val hashedBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(hashedBytes)
    }

    fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
        val computedHash = hashPassword(password, salt)
        return MessageDigest.isEqual(
            computedHash.toByteArray(Charsets.UTF_8),
            expectedHash.toByteArray(Charsets.UTF_8)
        )
    }

    fun sanitizeInput(input: String, maxLength: Int = 250): String {
        return input
            .replace("\u0000", "") // Strip null bytes
            .replace("(?i)<script[\\s\\S]*?>[\\s\\S]*?<\\/script>".toRegex(), "") // Strip script tags and content
            .replace("<[^>]*>".toRegex(), "") // Strip any HTML markup
            .trim()
            .take(maxLength)
    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.isNotBlank() && email.length <= 120 && emailRegex.matches(email.trim())
    }

    fun validatePasswordStrength(password: String): PasswordValidationResult {
        if (password.length < 8) {
            return PasswordValidationResult.Weak("Password must be at least 8 characters long")
        }
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        if (!hasLetter || !hasDigit) {
            return PasswordValidationResult.Weak("Password must contain both letters and numbers")
        }
        return PasswordValidationResult.Valid
    }
}

sealed interface PasswordValidationResult {
    object Valid : PasswordValidationResult
    data class Weak(val reason: String) : PasswordValidationResult
}
