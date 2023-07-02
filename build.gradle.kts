plugins {
    java
    idea
    kotlin("jvm") apply false
    id("org.springframework.boot") version "3.1.1" apply false
    id("io.spring.dependency-management") version "1.1.0" apply false
    id("com.github.jakemarsden.git-hooks") version "0.0.2"
}

tasks.register<TestReport>("testReport") {
    destinationDirectory.set(file("$buildDir/reports/tests"))
    testResults.from(subprojects.flatMap { it.tasks.withType<Test>().matching { it.name == "test" } })
}

tasks.named("test").configure {
    dependsOn("testReport")
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "jacoco")
    apply(plugin = "checkstyle")

    dependencies {
        implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
        annotationProcessor("org.projectlombok:lombok")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        reports.html.required.set(false)
        reports.junitXml.required.set(false)
    }
}

allprojects {
    group = "com.nytdacm"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://repo.spring.io/milestone")
    }
}

idea {
    module {
        // 忽略前端构建文件
        excludeDirs = setOf(
            file(".hooks"),
            file("$projectDir/oa-app/src/main/resources/static"),
            file("build"),
            file("$projectDir/web/dist"),
        )
    }
}

gitHooks {
    setHooks(mapOf("pre-commit" to "ktlintFormat"))
}
