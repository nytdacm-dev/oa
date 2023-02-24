package com.nytdacm.oa.buildsupport

import org.gradle.api.Project

fun Project.isCI() = System.getenv("CI") != null

fun Project.getEnvironment() = System.getenv("ENV") ?: ENV_DEVELOPMENT

val Project.ENV_PRODUCTION: String
    get() = "prod"

val Project.ENV_DEVELOPMENT: String
    get() = "dev"
