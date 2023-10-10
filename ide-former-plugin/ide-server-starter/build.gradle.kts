group = rootProject.group
version = rootProject.version

dependencies {
    implementation("com.github.ajalt:clikt:2.8.0")
    implementation("org.jetbrains.research:plugin-utilities-core:2.0.6")
    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation(project(":ide-server"))
}

abstract class IOCliTask : org.jetbrains.intellij.tasks.RunIdeTask() {
    @get:Input
    val runner: String? by project

    @get:Input
    val input: String? by project

    @get:Input
    val serverHost: String? by project

    @get:Input
    val serverPort: String? by project

    init {
        jvmArgs = listOf(
            "-Djava.awt.headless=true",
            "--add-exports",
            "java.base/jdk.internal.vm=ALL-UNNAMED",
            "-Djdk.module.illegalAccess.silent=true"
        )
        maxHeapSize = "2g"
        standardInput = System.`in`
        standardOutput = System.`out`
    }
}

tasks {
    register<IOCliTask>("runIdeAssistantPlugin") {
        dependsOn("buildPlugin")
        args = listOfNotNull(
            runner,
            input,
            serverHost,
            serverPort
        )
    }
}
