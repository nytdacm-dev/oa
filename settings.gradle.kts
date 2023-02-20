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

include("oa-common")
include("oa-app")
include("oa-config")
include("oa-dao")
include("oa-service")
include("oa-service-impl")
include("oa-third-part")
include("oa-utils")
