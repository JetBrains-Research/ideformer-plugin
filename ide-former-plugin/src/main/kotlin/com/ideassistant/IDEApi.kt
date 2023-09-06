package com.ideassistant

import com.intellij.openapi.components.Service
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

object IDEApis {
    fun getAllProjectModules(project: Project): List<Module> = ModuleManager.getInstance(project).modules.toList()

    fun getAllModuleFiles(module: Module): List<VirtualFile> =
        FileTypeIndex.getFiles(KotlinFileType.INSTANCE, module.moduleScope).toList()

    // TODO: better to search only by full equality of module names.
    //  'Contains' was added as a temporary solution for model and IDE interaction example
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

interface IDEApiMethod
object GetAllProjectModules : IDEApiMethod
data class GetAllModuleFiles(val moduleName: String) : IDEApiMethod
data class GetAllKtFileKtMethods(val ktFileName: String) : IDEApiMethod
data class SaveModelFinalAns(val modelFinalAns: String) : IDEApiMethod

@Service(Service.Level.PROJECT)
class IDEApiExecutorService(private val userProject: Project) {
    private val apiCallList: MutableList<IDEApiMethod> = mutableListOf()

    fun executeApiMethod(apiMethod: IDEApiMethod): String {
        val methodCallRes: Any = when (apiMethod) {
            is GetAllProjectModules -> {
                IDEApis.getAllProjectModules(userProject)
            }

            is GetAllModuleFiles -> {
                val module = IDEApis.getProjectModuleByNameContaining(userProject, apiMethod.moduleName)
                IDEApis.getAllModuleFiles(module)
            }

            is GetAllKtFileKtMethods -> {
                // TODO: in the future, when the current location (state) will be saved,
                //  search only in the curr dir/module, not with a global search
                val ktFile = IDEApis.getKtFileByName(userProject, apiMethod.ktFileName)
                val ktMethods = IDEApis.getKtFileKtMethods(ktFile)
                ktMethods.map { it.name }
            }

            is SaveModelFinalAns -> {
                // TODO: to save model final ans somewhere + display it on the ToolWindow
            }

            else -> "Method cannot be called"
        }

        apiCallList.add(apiMethod)
        return methodCallRes.toString()
    }
}
