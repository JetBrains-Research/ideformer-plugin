package com.ideAssistant

import com.intellij.openapi.application.ApplicationStarter

object PluginRunner : ApplicationStarter {
    @Deprecated("Specify it as `id` for extension definition in a plugin descriptor")
    override val commandName: String
        // TODO: rename
        get() = "DemoPluginCLI"

    override val requiredModality: Int
        get() = ApplicationStarter.NOT_IN_EDT

    override fun main(args: List<String>) {
//        userProject.service<IdeServerService>().startServer()
    }
}