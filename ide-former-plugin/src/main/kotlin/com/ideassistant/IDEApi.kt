package com.ideassistant

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
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

    fun getAllProjectFiles(project: Project): List<VirtualFile> =
        getAllProjectModules(project)
            .map { getAllModuleFiles(it) }
            .flatten()

    fun getAllProjectKtMethods(project: Project): List<KtNamedFunction> {
        val ktFunList = mutableListOf<KtNamedFunction>()
        getAllProjectFiles(project)
            .map { PsiManager.getInstance(project).findFile(it) }
            .filterIsInstance<KtFile>()
            .forEach {
                PsiTreeUtil.findChildOfType(it, KtNamedFunction::class.java)
            }
        return ktFunList
    }

    fun getProjectFileByName(project: Project, fileName: String): VirtualFile? =
        getAllProjectFiles(project).firstOrNull { it.name == fileName }
}


interface IDEApiMethod {
    fun call(): String
}

data class GetAllProjectModules(val project: Project) : IDEApiMethod {
    override fun call(): String =
        IDEApis.getAllProjectModules(this.project).toString()
}

data class GetAllProjectFiles(val project: Project) : IDEApiMethod {
    override fun call(): String =
        IDEApis.getAllProjectFiles(this.project).toString()
}

data class GetAllModuleFiles(val module: Module) : IDEApiMethod {
    override fun call(): String =
        IDEApis.getAllModuleFiles(this.module).toString()
}

data class GetAllProjectKtMethods(val project: Project) : IDEApiMethod {
    override fun call(): String =
        IDEApis.getAllProjectKtMethods(this.project).toString()
}

data class GetProjectFileByName(val project: Project, val fileName: String) : IDEApiMethod {
    override fun call(): String =
        IDEApis.getProjectFileByName(this.project, this.fileName).toString()
}
