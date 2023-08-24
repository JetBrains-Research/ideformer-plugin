package com.ideassistant

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.psi.search.FileTypeIndex
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import com.intellij.openapi.module.Module
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.util.PsiTreeUtil

object IDEApis {
    fun getAllProjectModules(project: Project): List<Module> = ModuleManager.getInstance(project).modules.toList()

    fun getAllModuleFiles(module: Module): List<VirtualFile> =
        FileTypeIndex.getFiles(KotlinFileType.INSTANCE, module.moduleScope).toList()

    fun getProjectModuleByName(project: Project, moduleName: String): Module? =
        getAllProjectModules(project).firstOrNull { it.name == moduleName }

    fun getKtFileKtMethods(ktFile: KtFile): List<KtNamedFunction> =
        PsiTreeUtil.findChildrenOfType(ktFile, KtNamedFunction::class.java).toList()
}
class IdeApiExecutor(private var userProject: Project) {
    fun updateProject(updatedProject: Project) {
        userProject = updatedProject
    }

    fun executeApiMethod(apiMethod: IDEApiMethod): String {
        val methodCallRes: Any = when (apiMethod) {
            is GetAllProjectModules -> {
                IDEApis.getAllProjectModules(this.userProject)
            }

            is GetAllModuleFiles -> {
                val module = IDEApis.getProjectModuleByName(this.userProject, apiMethod.moduleName) ?: throw IllegalArgumentException("No such module")
                IDEApis.getAllModuleFiles(module)
            }
            else -> "Method cannot be called"
        }

        return methodCallRes.toString()
    }
}

interface IDEApiMethod
object GetAllProjectModules : IDEApiMethod
data class GetAllModuleFiles(val moduleName: String) : IDEApiMethod
