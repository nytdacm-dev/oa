package com.nytdacm.oa.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
open class FileConfig(
    @Value("\${file.upload-dir}") val uploadDir: String,
)
