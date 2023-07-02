// TODO: https://spring.io/guides/tutorials/spring-boot-kotlin/
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

extensions.configure<JavaPluginExtension>("java") {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val kotlinVersion = "1.8.20"
val coroutinesVersion = "1.6.4"

dependencies {
    add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    add("implementation", "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutinesVersion")
    add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        freeCompilerArgs += "-Xemit-jvm-type-annotations"
    }
}
