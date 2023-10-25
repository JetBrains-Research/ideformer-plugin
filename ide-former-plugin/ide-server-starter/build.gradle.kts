group = rootProject.group
version = rootProject.version

dependencies {
    implementation(rootProject.libs.ajalt.clikt)
    implementation(rootProject.libs.plugin.utilities.core)
    implementation(rootProject.libs.slf4j.simple)

    implementation(project(":ide-server"))
}

abstract class IOCliTask : org.jetbrains.intellij.tasks.RunIdeTask() {
    @get:Input
    val runner: String? by project

    @get:Input
    val pathToProject: String? by project

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
            pathToProject,
            serverHost,
            serverPort
        )
    }
}
