package com.nytdacm.oa.service

interface ReCaptchaService {
    fun verify(secret: String, response: String, remoteIp: String? = null): Boolean
}
