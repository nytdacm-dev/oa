package com.nytdacm.oa.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

public class PasswordUtil {
    public static String getSalt() {
        return RandomStringUtils.random(32, true, true);
    }

    public static String hashPassword(String password, String salt) {
        return DigestUtils.sha256Hex(password + salt);
    }

    public static boolean checkPassword(String password, String salt, String hash) {
        return hashPassword(password, salt).equals(hash);
    }
}
