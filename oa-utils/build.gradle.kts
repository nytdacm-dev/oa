plugins {
    id("configure-kotlin")
}

dependencies {
    implementation(project(":oa-common"))
    implementation("commons-codec:commons-codec:1.15")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
}
