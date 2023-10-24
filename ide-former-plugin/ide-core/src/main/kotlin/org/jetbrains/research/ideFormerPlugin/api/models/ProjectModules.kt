package org.jetbrains.research.ideFormerPlugin.api.models

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project

class ProjectModules(private val project: Project) : IdeApiMethod {
    private lateinit var projectModules: List<Module>
    private fun Project.modules(): List<Module> = ModuleManager.getInstance(this).modules.toList()

    override fun execute() {
        projectModules = project.modules()
    }

    fun getProjectModulesNames(): List<String> = projectModules.map { it.name }.toList()
}
