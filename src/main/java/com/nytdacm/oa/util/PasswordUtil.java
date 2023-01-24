package com.nytdacm.oa.util;

import org.apache.commons.codec.digest.DigestUtils;

public class PasswordUtil {
    public static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }

    public static boolean checkPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }
}
