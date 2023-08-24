package com.ideassistant

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.psi.search.FileTypeIndex
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import com.intellij.openapi.module.Module
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil

object IDEApis {
    fun getAllProjectModules(project: Project): List<Module> = ModuleManager.getInstance(project).modules.toList()

    fun getAllModuleFiles(module: Module): List<VirtualFile> =
        FileTypeIndex.getFiles(KotlinFileType.INSTANCE, module.moduleScope).toList()

    // TODO: better to search only by full equality of module names.
    //  'Contains' was added as a temporary solution to make queries in ScenarioGenerator work for everyone.
    fun getProjectModuleByNameContaining(project: Project, moduleName: String): Module =
        getAllProjectModules(project).first { it.name == moduleName || it.name.contains(moduleName) }

    fun getKtFileKtMethods(ktFile: KtFile): List<KtNamedFunction> =
        PsiTreeUtil.findChildrenOfType(ktFile, KtNamedFunction::class.java).toList()

    fun getKtFileByName(project: Project, ktFileName: String): KtFile =
        getAllProjectModules(project)
            .flatMap { getAllModuleFiles(it) }
            .filter { it.name.contains(ktFileName) }
            .map { PsiManager.getInstance(project).findFile(it) }
            .filterIsInstance<KtFile>()
            .first()

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
                val module = IDEApis.getProjectModuleByNameContaining(this.userProject, apiMethod.moduleName)
                IDEApis.getAllModuleFiles(module)
            }

            is GetAllKtFileKtMethods -> {
                // TODO: in the future, when the current location (state) will be saved,
                //  search only in the curr dir/module, not with a global search
                val ktFile = IDEApis.getKtFileByName(this.userProject, apiMethod.ktFileName)
                val ktMethods = IDEApis.getKtFileKtMethods(ktFile)
                ktMethods.map { it.name }
            }

            else -> "Method cannot be called"
        }

        return methodCallRes.toString()
    }
}

interface IDEApiMethod
object GetAllProjectModules : IDEApiMethod
data class GetAllModuleFiles(val moduleName: String) : IDEApiMethod
data class GetAllKtFileKtMethods(val ktFileName: String) : IDEApiMethod
