plugins {
    java
    checkstyle
    jacoco
    idea

    id("org.springframework.boot") version "3.0.2"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "com.nytdacm"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "groovy")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.springframework.boot")

    val groovyVersion = "4.0.8"

    dependencies {
        implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
        annotationProcessor("org.projectlombok:lombok")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.apache.groovy:groovy-test:$groovyVersion")
        testImplementation("org.apache.groovy:groovy-test-junit5:$groovyVersion")
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    group = "com.nytdacm"
    version = "0.0.1-SNAPSHOT"
    java.sourceCompatibility = JavaVersion.VERSION_17

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
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
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
