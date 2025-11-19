@file:OptIn(ExperimentalStdlibApi::class)

package bz.apps.busfare.rw.helpers

import bz.Config
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Cryption {

    private val key get() = Config.getString("KEY")!!.toByteArray() // 16 bytes key for AES-128
    private val iv get() = Config.getString("IV")!!.toByteArray()  // 16 bytes initialization vector

    private fun encryptInternal(plaintext: ByteArray, key: ByteArray, iv: ByteArray): ByteArray? {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val encryptedBytes = cipher.doFinal(plaintext)
        return encryptedBytes
    }

    private fun decryptInternal(hex: ByteArray, key: ByteArray, iv: ByteArray): ByteArray? {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val decryptedBytes = cipher.doFinal(hex)
        return decryptedBytes
    }


    fun encrypt(bytes: ByteArray): String? {
        return encryptInternal(bytes, key, iv)?.toHexString(HexFormat.UpperCase)
    }

    fun decrypt(encodedBytes: String?): ByteArray? {
        val value = encodedBytes?.hexToByteArray()
        return decryptInternal(value?: throw NullPointerException("Hex cannot be null"), key, iv)
    }

    fun encryptHex(plaintext: String): String? {
        return encryptInternal(plaintext.toByteArray(), key, iv)?.toHexString()
    }

    fun decryptHex(hex: String?): String? {
        val value = hex?.hexToByteArray()
        return String(decryptInternal(value?: throw NullPointerException("Hex cannot be null"), key, iv) ?:return null)
    }
}