package com.ideassistant

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
import java.nio.file.Files
import java.nio.file.Paths

interface IDEApiMethod {
    fun execute(): String
}

interface ReversibleApiMethod {
    fun reverse()
}

class GetAllProjectModules(private val project: Project) : IDEApiMethod {
    companion object {
        fun getAllProjectModules(project: Project): List<Module> =
            ModuleManager.getInstance(project).modules.toList()
    }

    override fun execute(): String = getAllProjectModules(project).toString()
}

class GetAllModuleFiles(private val project: Project, moduleName: String) : IDEApiMethod {
    private val module = getProjectModuleByNameContaining(moduleName)

    // TODO: better to search only by full equality of module names.
    //  'Contains' was added as a temporary solution for model and IDE interaction example
    private fun getProjectModuleByNameContaining(moduleName: String): Module {
        val projectModules = GetAllProjectModules.getAllProjectModules(project)
        return projectModules.first { it.name == moduleName || it.name.contains(moduleName) }
    }

    companion object {
        fun getAllModuleFiles(module: Module): List<VirtualFile> =
            FileTypeIndex.getFiles(KotlinFileType.INSTANCE, module.moduleScope).toList()
    }

    override fun execute(): String = getAllModuleFiles(module).toString()
}

class GetAllKtFileKtMethods(private val project: Project, ktFileName: String) : IDEApiMethod {
    private val ktFile = getKtFileByName(ktFileName)

    // TODO: search only in a dir
    private fun getKtFileByName(ktFileName: String): KtFile {
        val projectModules = GetAllProjectModules.getAllProjectModules(project)
        return projectModules
            .flatMap { GetAllModuleFiles.getAllModuleFiles(it) }
            .filter { it.name.contains(ktFileName) }
            .map { PsiManager.getInstance(project).findFile(it) }
            .filterIsInstance<KtFile>()
            .first()
    }

    companion object {
        fun getKtFileKtMethods(ktFile: KtFile): List<KtNamedFunction> =
            PsiTreeUtil.findChildrenOfType(ktFile, KtNamedFunction::class.java).toList()
    }

    override fun execute(): String = getKtFileKtMethods(ktFile)
        .map { it.name }
        .toString()
}

class ChangeDirectory(private val project: Project, private val targetDirectory: String) : IDEApiMethod, ReversibleApiMethod {
    private var prevDirectory: String? = ideStateKeeper.curDirectory
    override fun execute(): String {
        val targetDirPath = Paths.get("${project.basePath}/$targetDirectory")
        if (Files.exists(targetDirPath)) {
            ideStateKeeper.curDirectory = targetDirectory
            return "Directory was changed: new dir is '$targetDirectory'"
        }
        return "No such directory in a project: $targetDirectory"
    }

    override fun reverse() {
        prevDirectory?.let {
            ideStateKeeper.curDirectory = it
            prevDirectory = null
        }
    }
}

class SaveModelFinalAns(private val modelFinalAns: String) : IDEApiMethod {
    override fun execute(): String {
        TODO("Not yet implemented")
    }
}