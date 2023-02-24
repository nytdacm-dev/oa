package com.nytdacm.oa.buildsupport

import org.gradle.api.Project

fun Project.isCI() = System.getenv("CI") != null

fun Project.getEnvironment() = findProperty("environment") ?: "dev"
