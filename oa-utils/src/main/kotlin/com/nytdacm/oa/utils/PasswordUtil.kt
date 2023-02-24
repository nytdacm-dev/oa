package com.nytdacm.oa.utils

import org.apache.commons.codec.digest.DigestUtils

object PasswordUtil {
    @JvmStatic
    val salt: String
        get() = randomString()

    @JvmStatic
    fun hashPassword(password: String, salt: String): String {
        return DigestUtils.sha256Hex(password + salt)
    }

    @JvmStatic
    fun checkPassword(password: String, salt: String, hash: String): Boolean {
        return hashPassword(password, salt) == hash
    }
}
