package com.ideassistant

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

interface IDEApiMethod {
    fun execute(): String
}

interface ReversibleApiMethod {
    fun reverse()
}

class GetAllProjectModules : IDEApiMethod {
    companion object {
        fun getAllProjectModules(project: Project): List<Module> =
            ModuleManager.getInstance(project).modules.toList()
    }

    override fun execute(): String =
        getAllProjectModules(ideStateKeeper.userProject).toString()
}

class GetAllModuleFiles(moduleName: String) : IDEApiMethod {
    private val module = getProjectModuleByNameContaining(moduleName)

    // TODO: better to search only by full equality of module names.
    //  'Contains' was added as a temporary solution for model and IDE interaction example
    private fun getProjectModuleByNameContaining(moduleName: String): Module {
        val projectModules = GetAllProjectModules.getAllProjectModules(ideStateKeeper.userProject)
        return projectModules.first { it.name == moduleName || it.name.contains(moduleName) }
    }

    companion object {
        fun getAllModuleFiles(module: Module): List<VirtualFile> =
            FileTypeIndex.getFiles(KotlinFileType.INSTANCE, module.moduleScope).toList()
    }

    override fun execute(): String = getAllModuleFiles(module).toString()
}

class GetAllKtFileKtMethods(ktFileName: String) : IDEApiMethod {
    private val ktFile = getKtFileByName(ktFileName)

    // TODO: search only in a current dir
    private fun getKtFileByName(ktFileName: String): KtFile {
        val projectModules = GetAllProjectModules.getAllProjectModules(ideStateKeeper.userProject)
        return projectModules
            .asSequence()
            .flatMap { GetAllModuleFiles.getAllModuleFiles(it) }
            .filter { it.name.contains(ktFileName) }
            .map { PsiManager.getInstance(ideStateKeeper.userProject).findFile(it) }
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

class ListDirectories(private val dirName: String = "") : IDEApiMethod {
    companion object {
        fun getListDirectories(psiDirectory: PsiDirectory): List<PsiFileSystemItem> {
            val files = psiDirectory.files.map { it as PsiFileSystemItem }
            val dirs = psiDirectory.subdirectories.map { it as PsiFileSystemItem }
            return files.plus(dirs)
        }
    }

    override fun execute(): String {
        val psiDirectory = when (dirName) {
            "" -> ideStateKeeper.curDirectory
            else -> ideStateKeeper.curDirectory.findSubdirectory(dirName) ?: throw Exception("No such subdirectory")
        }
        return getListDirectories(psiDirectory)
            .map { it.name }
            .toString()
    }
}

class ChangeDirectory(private val targetDirName: String) : IDEApiMethod, ReversibleApiMethod {
    private var prevDir: PsiDirectory? = null
    override fun execute(): String {
        prevDir = ideStateKeeper.curDirectory
        // a non-recursive search
        val targetDir = ideStateKeeper.curDirectory.findSubdirectory(targetDirName)
            ?: return "No such directory in a project: $targetDirName"
        ideStateKeeper.curDirectory = targetDir
        return "Directory was changed: new dir is '${targetDir.name}'"
    }

    override fun reverse() {
        prevDir?.let {
            ideStateKeeper.curDirectory = it
            prevDir = null
        }
    }
}

class SaveModelFinalAns(private val modelFinalAns: String) : IDEApiMethod, ReversibleApiMethod {
    override fun execute(): String {
        TODO("Not yet implemented")
    }

    override fun reverse() {
        TODO("Not yet implemented")
    }
}