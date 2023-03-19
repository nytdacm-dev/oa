plugins {
    id("configure-kotlin")
    id("configure-ktlint")
    id("configure-groovy")
}

val satokenVersion = "1.34.0"
val hutoolVersion = "5.8.15"

dependencies {
    implementation(project(":oa-common"))
    implementation(project(":oa-service"))
    implementation(project(":oa-service-impl"))
    implementation(project(":oa-utils"))
    implementation(project(":oa-config"))
    if (rootDir.resolve("oa-third-part/build.gradle.kts").isFile) {
        implementation(project(":oa-third-part"))
    }

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.5")
    implementation ("cn.dev33:sa-token-spring-boot3-starter:$satokenVersion")
    implementation("cn.dev33:sa-token-jwt:$satokenVersion") {
        // kotlin gradle exclude
        exclude("cn.hutool", "hutool-core")
        exclude("cn.hutool", "hutool-json")
    }
    implementation("cn.hutool:hutool-core:$hutoolVersion")
    implementation("cn.hutool:hutool-json:$hutoolVersion")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
}
