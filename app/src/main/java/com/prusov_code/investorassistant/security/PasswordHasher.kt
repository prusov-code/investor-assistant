package com.prusov_code.investorassistant.security

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {
    private const val ITERATIONS = 10000
    private const val KEY_LENGTH = 256
    private val random = SecureRandom()

    fun hash(password: String): Pair<String, String> {
        val salt = ByteArray(16).apply { random.nextBytes(this) }
        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            ITERATIONS,
            KEY_LENGTH
        )
        val key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(spec)
        val hash = key.encoded
        return Base64.encodeToString(hash, Base64.NO_WRAP) to
                Base64.encodeToString(salt, Base64.NO_WRAP)
    }

    fun verify(password: String, hash: String, salt: String): Boolean {
        val decodedHash = Base64.decode(hash, Base64.NO_WRAP)
        val decodedSalt = Base64.decode(salt, Base64.NO_WRAP)
        val spec = PBEKeySpec(
            password.toCharArray(),
            decodedSalt,
            ITERATIONS,
            KEY_LENGTH
        )
        val key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(spec)
        return key.encoded.contentEquals(decodedHash)
    }
}