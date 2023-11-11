package com.av.vtask.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class CryptoFactory {
    companion object {
        private const val KEY_ALIAS = "VTASK_ENCRYPTION_KEY"

        /**
         * This method check if there is an encryption key in the keyStore, and create one if there is no.
         * @return - whether there is a key at the end of the operation (either old or new).
         */
        fun generateAndStoreAESKey(): Boolean {
            return try {
                val keyStore = KeyStore.getInstance("AndroidKeyStore")
                keyStore.load(null)

                // Check if the key already exists in the KeyStore
                if (!keyStore.containsAlias(KEY_ALIAS)) {
                    val keyGenerator = KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES,
                        "AndroidKeyStore"
                    )

                    // Configure key generator
                    val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .setKeySize(256)
                        .build()

                    keyGenerator.init(keyGenParameterSpec)
                    keyGenerator.generateKey()
                }

                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        private fun retrieveKeyFromKeyStore(): SecretKey? {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            // Check if the key entry is not null and is a SecretKeyEntry
            return (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.secretKey
        }

        fun encrypt(plainText: String): String? {
            val key = retrieveKeyFromKeyStore()
            return if (key != null)
                encrypt(plainText, key)
            else
                null
        }

        private fun encrypt(input: String, secretKey: SecretKey): String {
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val encryptedBytes = cipher.doFinal(input.toByteArray(Charsets.UTF_8))

            //get the iv
            val iv = cipher.iv

            //return combined iv+cipher as string:
            val combinedBytes = ByteArray(iv.size + encryptedBytes.size)
            System.arraycopy(iv, 0, combinedBytes, 0, iv.size)
            System.arraycopy(encryptedBytes, 0, combinedBytes, iv.size, encryptedBytes.size)

            return Base64.getEncoder().encodeToString(combinedBytes)
        }

        fun decrypt(cipher: String): String? {
            val key = retrieveKeyFromKeyStore()
            return if (key != null) {
                decrypt(cipher, key)
            } else {
                null
            }
        }

        private fun decrypt(encryptedBase64: String, secretKey: SecretKey): String {
            val combinedBytes = Base64.getDecoder().decode(encryptedBase64)
            val iv = ByteArray(16)
            System.arraycopy(combinedBytes, 0, iv, 0, iv.size)

            val ivParameterSpec = IvParameterSpec(iv)
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)

            val decryptedBytes =
                cipher.doFinal(combinedBytes, iv.size, combinedBytes.size - iv.size)
            return String(decryptedBytes, Charsets.UTF_8)
        }
    }
}