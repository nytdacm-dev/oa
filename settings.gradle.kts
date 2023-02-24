pluginManagement {
    plugins {
        id("org.jetbrains.kotlin.jvm") version "1.8.10"
    }
}

plugins {
    id("com.gradle.enterprise") version "3.12.2"
}

gradleEnterprise {
    if (System.getenv("CI") != null) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}

rootProject.name = "oa"

require(JavaVersion.current() >= JavaVersion.VERSION_17) {
    "You must use at least Java 17 to build the project, you're currently using ${System.getProperty("java.version")}"
}

include("oa-common")
include("oa-app")
include("oa-config")
include("oa-dao")
include("oa-service")
include("oa-service-impl")
include("oa-utils")
if (settingsDir.resolve("oa-third-part/build.gradle.kts").isFile) {
    include("oa-third-part")
}
