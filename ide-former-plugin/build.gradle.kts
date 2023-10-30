group = "org.jetbrains.research.IdeAssistantPlugin"
version = "1.0"

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jetbrains.intellij)
    alias(libs.plugins.ktor)
}

allprojects {
    apply {
        plugin(rootProject.libs.plugins.kotlin.jvm.get().pluginId)
        plugin(rootProject.libs.plugins.jetbrains.intellij.get().pluginId)
        plugin(rootProject.libs.plugins.ktor.get().pluginId)
    }

    repositories {
        mavenCentral()
        maven("https://packages.jetbrains.team/maven/p/big-code/bigcode")
        maven("https://packages.jetbrains.team/maven/p/ki/maven")
    }

    // Configure Gradle IntelliJ Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
    intellij {
        version.set(properties("platformVersion"))
        type.set(properties("platformType"))
        downloadSources.set(properties("platformDownloadSources").toBoolean())
        updateSinceUntilBuild.set(true)
        plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
    }

    val jvmVersion = "17"

    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = jvmVersion
            }
        }

        withType<JavaCompile> {
            sourceCompatibility = jvmVersion
            targetCompatibility = jvmVersion
        }

        withType<org.jetbrains.intellij.tasks.BuildSearchableOptionsTask>()
            .forEach { it.enabled = false }
    }
}
