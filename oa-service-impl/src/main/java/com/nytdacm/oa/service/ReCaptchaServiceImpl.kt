package com.nytdacm.oa.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class ReCaptchaServiceImpl : ReCaptchaService {
    override fun verify(secret: String, response: String, remoteIp: String?): Boolean {
        // GET https://recaptcha.net/recaptcha/api/siteverify?secret={secret}&response={response}&remoteip={remoteIp}
        val httpclient = HttpClients.createDefault()
        try {
            val request = ClassicRequestBuilder
                .get(
                    "https://recaptcha.net/recaptcha/api/siteverify?secret=$secret&response=$response" +
                        if (remoteIp != null) "&remoteip=$remoteIp" else "",
                )
                .build()
            return httpclient.execute(request) {
                val status = it.code
                if (status == 200) {
                    // deserialize response with jackson
                    // {
                    //  "success": true|false,
                    //  "challenge_ts": timestamp,  // timestamp of the challenge load (ISO format yyyy-MM-dd'T'HH:mm:ssZZ)
                    //  "hostname": string,         // the hostname of the site where the reCAPTCHA was solved
                    //  "error-codes": [...]        // optional
                    // }
                    val body = it.entity.content.bufferedReader().use { t -> t.readText() }
                    val mapper = ObjectMapper()
                    val json = mapper.readTree(body)
                    return@execute json.get("success").asBoolean()
                }
                return@execute false
            }
        } catch (e: IOException) {
            return false
        } finally {
            httpclient.close()
        }
        return false
    }
}
