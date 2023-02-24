configurations.create("ktlint")
dependencies {
    val ktlintVersion = "0.48.2"
    "ktlint"("com.pinterest:ktlint:$ktlintVersion")
}

val ktlintTask = tasks.register<JavaExec>("ktlint") {
    group = "verification"
    description = "Check Kotlin code style"
    classpath = configurations["ktlint"]
    mainClass.set("com.pinterest.ktlint.Main")

    val output = buildDir.resolve("ktlint-report.txt")
    args("src/**/*.kt", "--reporter=plain", "--reporter=plain,output=${output.absolutePath}")

    inputs.files(fileTree("src") {
        include("**/*.kt")
    })
    outputs.file(output)

    mustRunAfter(tasks.withType<AbstractCompile>())
}

tasks.withType<Test>().configureEach {
    mustRunAfter(ktlintTask)
}

tasks.register<JavaExec>("ktlintFormat") {
    group = "formatting"
    description = "Fix Kotlin code style deviations"
    classpath = configurations["ktlint"]
    mainClass.set("com.pinterest.ktlint.Main")
    args("-F", "src/**/*.kt")
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}
