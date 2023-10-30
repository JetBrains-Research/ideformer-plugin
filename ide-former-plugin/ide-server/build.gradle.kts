group = rootProject.group
version = rootProject.version

dependencies {
    implementation(rootProject.libs.ktor.server.core)
    implementation(rootProject.libs.ktor.server.netty)
    implementation(rootProject.libs.gson)

    implementation(project(":ide-core"))
}