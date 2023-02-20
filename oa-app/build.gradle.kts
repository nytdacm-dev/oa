plugins {
    `maven-publish`
    id("org.springframework.boot.experimental.thin-launcher") version "1.0.29.RELEASE"
}

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
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.2")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
