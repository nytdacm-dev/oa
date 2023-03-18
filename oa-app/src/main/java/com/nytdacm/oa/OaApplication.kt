package com.nytdacm.oa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableCaching
open class OaApplication

fun main(args: Array<String>) {
    runApplication<OaApplication>(*args)
}
