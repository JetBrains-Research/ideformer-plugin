package org.jetbrains.research.ideFormerPlugin.server

import com.intellij.openapi.components.Service
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class IdeServerService(private val project: Project) {
    private lateinit var ideServer: IdeServer

    fun startServer(host: String = "localhost", port: Int = 8082) {
        object : Task.Backgroundable(project, "IDE server start") {
            override fun run(indicator: ProgressIndicator) {
                ideServer = IdeServer(host, port)
                ideServer.startServer(project)
            }
        }.queue()
    }
}