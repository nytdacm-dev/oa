package com.nytdacm.oa.utils

import java.util.UUID

fun randomString() = UUID.randomUUID().toString().replace("-", "")
