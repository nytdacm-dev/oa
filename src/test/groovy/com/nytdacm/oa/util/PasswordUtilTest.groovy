package com.nytdacm.oa.util


import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertTrue

class PasswordUtilTest {
    @Test
    void testHashPassword() {
        assertEquals("96cae35ce8a9b0244178bf28e4966c2ce1b8385723a96a6b838858cdd6ca0a1e",
            PasswordUtil.hashPassword("123", "123"))
    }

    @Test
    void testCheckPassword() {
        assertTrue(PasswordUtil.checkPassword("123", "123",
            "96cae35ce8a9b0244178bf28e4966c2ce1b8385723a96a6b838858cdd6ca0a1e"))
        assertFalse(PasswordUtil.checkPassword("123", "123", "123"))
    }
}
