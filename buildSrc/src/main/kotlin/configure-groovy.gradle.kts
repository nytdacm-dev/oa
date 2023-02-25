plugins {
    id("groovy")
}

val groovyVersion = "4.0.8"
dependencies {
    add("testImplementation", "org.apache.groovy:groovy-test:$groovyVersion")
    add("testImplementation", "org.apache.groovy:groovy-test-junit5:$groovyVersion")
}
