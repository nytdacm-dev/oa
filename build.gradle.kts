plugins {
    java
    groovy
    `maven-publish`
    checkstyle
    jacoco
    idea

    id("org.springframework.boot") version "3.0.2"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.springframework.boot.experimental.thin-launcher") version "1.0.29.RELEASE"
    id("org.hibernate.orm") version "6.1.6.Final"
}

group = "com.nytdacm"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    if (System.getenv("CI") == null) {
        // 本地开发时使用阿里云仓库
        maven(url = "https://maven.aliyun.com/repository/public/")
        maven(url = "https://maven.aliyun.com/repository/spring/")
    }
    mavenCentral()
    maven(url = "https://repo.spring.io/snapshot")
    maven(url = "https://repo.spring.io/milestone")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

val satokenVersion = "1.34.0"
val hutoolVersion = "5.8.12"
val groovyVersion = "4.0.8"

dependencies {
    implementation("org.apache.groovy:groovy")
    implementation("cn.dev33:sa-token-spring-boot3-starter:$satokenVersion")
    implementation("cn.dev33:sa-token-jwt:$satokenVersion") {
        // kotlin gradle exclude
        exclude("cn.hutool", "hutool-core")
        exclude("cn.hutool", "hutool-json")
    }
    implementation("cn.hutool:hutool-core:$hutoolVersion")
    implementation("cn.hutool:hutool-json:$hutoolVersion")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    implementation("org.jsoup:jsoup:1.15.3")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.2")
    implementation("commons-codec:commons-codec:1.15")
    implementation("commons-lang:commons-lang:2.6")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.apache.groovy:groovy-test:$groovyVersion")
    testImplementation("org.apache.groovy:groovy-test-junit5:$groovyVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

hibernate {
    enhancement {
        enableLazyInitialization(true)
        enableDirtyTracking(true)
        enableAssociationManagement(true)
    }
}

idea {
    module {
        // 忽略前端构建文件
        excludeDirs = setOf(file("$projectDir/src/main/resources/static"), file("$projectDir/web/dist"))
    }
}

tasks.named("check").configure {
    dependsOn("jacocoTestReport")
}
