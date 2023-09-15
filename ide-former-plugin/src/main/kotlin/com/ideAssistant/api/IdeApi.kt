package com.ideAssistant.api

import com.ideAssistant.server.IdeStateKeeper
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

interface IdeApiMethod {
    fun execute()
    fun getExecutionRes(): String
}

interface ReversibleApiMethod {
    fun reverse()
}

class GetProjectModules(private val project: Project) : IdeApiMethod {
    private lateinit var projectModules: List<Module>

    companion object {
        fun getProjectModules(project: Project): List<Module> =
            ModuleManager.getInstance(project).modules.toList()
    }

    override fun execute() {
        projectModules = getProjectModules(project)
    }

    override fun getExecutionRes(): String = projectModules.toString()
}

class GetKtFileKtMethods(
    private val curDirectory: PsiDirectory,
    private val ktFileName: String
) : IdeApiMethod {
    private lateinit var fileKtMethods: List<KtNamedFunction>

    private fun getKtFileByName(ktFileName: String): KtFile =
        curDirectory.files
            .filterIsInstance<KtFile>()
            .firstOrNull { it.name == ktFileName }
            ?: throw Exception("No such file in the current directory")

    companion object {
        fun getKtFileKtMethods(ktFile: KtFile): List<KtNamedFunction> =
            PsiTreeUtil.findChildrenOfType(ktFile, KtNamedFunction::class.java).toList()
    }

    override fun execute() {
        val ktFile = getKtFileByName(ktFileName)
        fileKtMethods = getKtFileKtMethods(ktFile)
    }

    internal fun getMethodsNames() = fileKtMethods.map { it.name }
    override fun getExecutionRes(): String = getMethodsNames().toString()
}

class ListDirectoryContents(
    private val curDirectory: PsiDirectory,
    private val dirName: String = "."
) : IdeApiMethod {
    private lateinit var dirContents: List<PsiFileSystemItem>

    companion object {
        fun getListDirectoryContents(psiDirectory: PsiDirectory): List<PsiFileSystemItem> {
            val files = psiDirectory.files.map { it as PsiFileSystemItem }
            val dirs = psiDirectory.subdirectories.map { it as PsiFileSystemItem }
            return files.plus(dirs)
        }
    }

    override fun execute() {
        val psiDirectory = when (dirName) {
            "." -> curDirectory
            else -> PsiElementsUtils.findSubdirectory(curDirectory, dirName) ?: throw Exception("No such subdirectory")
        }
        dirContents = getListDirectoryContents(psiDirectory)
    }

    internal fun getDirContentsNames() = dirContents.map { it.name }

    override fun getExecutionRes(): String = getDirContentsNames().toString()
}

class ChangeDirectory(
    private val ideStateKeeper: IdeStateKeeper,
    private val targetDirName: String = "."
) : IdeApiMethod, ReversibleApiMethod {
    private var prevDir: PsiDirectory? = null

    override fun execute() {
        if (targetDirName == ".") {
            return
        }

        val targetDir = PsiElementsUtils.findSubdirectory(ideStateKeeper.curDirectory, targetDirName)
            ?: throw Exception("No such directory in a project: $targetDirName.")

        prevDir = ideStateKeeper.curDirectory
        ideStateKeeper.curDirectory = targetDir
    }

    override fun getExecutionRes(): String =
        when (targetDirName) {
            "." -> "Working directory remains the same."
            else -> "Working directory was changed to $targetDirName."
        }

    override fun reverse() {
        prevDir?.let {
            ideStateKeeper.curDirectory = it
            prevDir = null
        }
    }
}

class SaveModelFinalAns(private val modelFinalAns: String) : IdeApiMethod, ReversibleApiMethod {
    override fun execute() {
        TODO("Not yet implemented")
    }

    override fun getExecutionRes(): String {
        TODO("Not yet implemented")
    }

    override fun reverse() {
        TODO("Not yet implemented")
    }
}