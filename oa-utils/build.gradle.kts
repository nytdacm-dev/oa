plugins {
    id("configure-kotlin")
    id("configure-ktlint")
}

dependencies {
    implementation(project(":oa-common"))
    implementation("commons-codec:commons-codec:1.16.0")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
}
