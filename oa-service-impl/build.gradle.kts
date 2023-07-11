plugins {
    id("configure-groovy")
    id("configure-spock")
    id("configure-kotlin")
    id("configure-ktlint")
}

dependencies {
    implementation(project(":oa-common"))
    implementation(project(":oa-service"))
    implementation(project(":oa-dao"))
    implementation(project(":oa-utils"))
    implementation(project(":oa-config"))
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
}
