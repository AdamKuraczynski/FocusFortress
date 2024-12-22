package com.adamkuraczynski.focusfortress.strictness

import android.util.Base64
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object HashingUtility {
    private const val ITERATIONS = 10000
    private const val KEY_LENGTH = 256

    fun generateSalt(): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return Base64.encodeToString(salt, Base64.NO_WRAP)
    }

    fun hashPasscode(passcode: String, salt: String): String {
        val spec = PBEKeySpec(passcode.toCharArray(), Base64.decode(salt, Base64.NO_WRAP), ITERATIONS, KEY_LENGTH)
        return try {
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val hash = factory.generateSecret(spec).encoded
            Base64.encodeToString(hash, Base64.NO_WRAP)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Hashing algorithm not found.", e)
        }
    }

    fun verifyPasscode(passcode: String, salt: String, expectedHash: String): Boolean {
        val hash = hashPasscode(passcode, salt)
        return hash == expectedHash
    }
}
