package com.anhhoang.aws.feature.s3reader.impl.common

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

/**
 * Cryptographic service to save and retrieve sensitive data.
 *
 * Internal since no where else is using it. This needs refactor out if it's needed elsewhere too.
 */
internal object CryptoService {
    private const val ANDROID_KEY_STORE = "AndroidKeyStore"
    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
    private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"

    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
        load(null)
    }

    private val encryptCipher
        get() = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getKey())
        }

    private fun getDecryptCipherForIv(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry("secret", null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    "secret",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    fun encrypt(bytes: ByteArray, outputStream: OutputStream): ByteArray {
        val encryptedBytes = encryptCipher.doFinal(bytes)
        outputStream.use {
            it.write(encryptCipher.iv.size)
            it.write(encryptCipher.iv)
            it.write(encryptedBytes.size)
            it.write(encryptedBytes)
        }
        return encryptedBytes
    }

    fun decrypt(inputStream: InputStream): ByteArray = inputStream.use {
        val ivSize = it.read()
        val iv = ByteArray(ivSize)
        it.read(iv)

        val encryptedBytesSize = it.read()
        val encryptedBytes = ByteArray(encryptedBytesSize)
        it.read(encryptedBytes)

        getDecryptCipherForIv(iv).doFinal(encryptedBytes)
    }
}