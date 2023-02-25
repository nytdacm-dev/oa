plugins {
    id("configure-groovy")
    id("configure-spock")
}

val satokenVersion = "1.34.0"
val hutoolVersion = "5.8.12"

dependencies {
    implementation(project(":oa-common"))
    implementation(project(":oa-service"))
    implementation(project(":oa-dao"))
    implementation(project(":oa-utils"))
    implementation(project(":oa-config"))

    api("cn.dev33:sa-token-spring-boot3-starter:$satokenVersion")
    api("cn.dev33:sa-token-jwt:$satokenVersion") {
        // kotlin gradle exclude
        exclude("cn.hutool", "hutool-core")
        exclude("cn.hutool", "hutool-json")
    }
    api("cn.hutool:hutool-core:$hutoolVersion")
    api("cn.hutool:hutool-json:$hutoolVersion")
}
