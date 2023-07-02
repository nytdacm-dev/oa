plugins {
    `kotlin-dsl`
    `java-library`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.20")
    implementation(gradleApi())
}
