package org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiMethod

class ProjectModules(private val project: Project) : IdeApiMethod {
    private var projectModules: List<Module>? = null
    private fun Project.modules(): List<Module> = ModuleManager.getInstance(this).modules.toList()

    override fun execute() {
        projectModules = project.modules()
    }

    fun getProjectModulesNames(): List<String>? = projectModules?.map { it.name }?.toList()
}
