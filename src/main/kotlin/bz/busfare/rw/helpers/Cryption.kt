@file:OptIn(ExperimentalEncodingApi::class, ExperimentalStdlibApi::class)

package bz.busfare.rw.helpers

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.ExperimentalEncodingApi

object Cryption {


    fun encryptInternal(plaintext: ByteArray, key: ByteArray, iv: ByteArray): ByteArray? {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val encryptedBytes = cipher.doFinal(plaintext)
        return encryptedBytes
    }

    fun decryptInternal(hex: ByteArray, key: ByteArray, iv: ByteArray): ByteArray? {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val decryptedBytes = cipher.doFinal(hex)
        return decryptedBytes
    }


    fun encrypt(bytes: ByteArray): String? {
        val key = "Bt43zC4rDTr4nZ1T".toByteArray() // 16 bytes key for AES-128
        val iv = "ILoveBelize!2024".toByteArray()  // 16 bytes initialization vector
        return encryptInternal(bytes, key, iv)?.toHexString(HexFormat.UpperCase)
    }

    fun decrypt(encodedBytes: String?): ByteArray? {
        val key = "Bt43zC4rDTr4nZ1T".toByteArray() // 16 bytes key for AES-128
        val iv = "ILoveBelize!2024".toByteArray()  // 16 bytes initialization vector
        val value = encodedBytes?.hexToByteArray()
        return decryptInternal(value?: throw NullPointerException("Hex cannot be null"), key, iv)
    }

    fun encryptHex(plaintext: String): String? {
        val key = "Bt43zC4rDTr4nZ1T".toByteArray() // 16 bytes key for AES-128
        val iv = "ILoveBelize!2024".toByteArray()  // 16 bytes initialization vector
        return encryptInternal(plaintext.toByteArray(), key, iv)?.toHexString()
    }

    fun decryptHex(hex: String?): String? {
        val key = "Bt43zC4rDTr4nZ1T".toByteArray() // 16 bytes key for AES-128
        val iv = "ILoveBelize!2024".toByteArray()  // 16 bytes initialization vector
        val value = hex?.hexToByteArray()
        return String(decryptInternal(value?: throw NullPointerException("Hex cannot be null"), key, iv) ?:return null)
    }
}