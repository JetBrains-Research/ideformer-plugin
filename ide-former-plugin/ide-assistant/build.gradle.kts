group = rootProject.group
version = rootProject.version

dependencies {
    implementation("com.aallam.openai:openai-client:3.1.1")
    implementation("io.ktor:ktor-server-core:2.3.3")
    implementation("io.ktor:ktor-server-netty:2.3.3")
    implementation("io.ktor:ktor-client-core:2.3.3")
    implementation("io.ktor:ktor-client-cio:2.3.3")
    implementation("com.google.code.gson:gson:2.10.1")
}

//tasks.test {
//    useJUnitPlatform()
//}