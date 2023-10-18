package org.jetbrains.research.ideFormerPlugin.api

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper

interface IdeApiMethod {
    fun execute()
    fun executionResult(): String
}

interface ReversibleApiMethod : IdeApiMethod {
    fun reverse()
}

class ProjectModules(private val project: Project) : IdeApiMethod {
    private lateinit var projectModules: List<Module>
    private fun Project.modules(): List<Module> = ModuleManager.getInstance(this).modules.toList()

    override fun execute() {
        projectModules = project.modules()
    }

    override fun executionResult(): String = projectModules.toString()
}

class KtFileKtMethods(
    private val projectDirectory: PsiDirectory,
    private val ktFileName: String
) : IdeApiMethod {
    private lateinit var fileKtMethods: List<KtNamedFunction>

    private fun findKtFileByName(ktFileName: String): KtFile =
        projectDirectory.files
            .filterIsInstance<KtFile>()
            .firstOrNull { it.name == ktFileName }
            ?: error("No such file in the current directory")

    private fun KtFile.ktNamedFunctions(): List<KtNamedFunction> =
        PsiTreeUtil.findChildrenOfType(this, KtNamedFunction::class.java).toList()

    override fun execute() {
        findKtFileByName(ktFileName).also {
            fileKtMethods = it.ktNamedFunctions()
        }
    }

    internal fun getMethodsNames() = fileKtMethods.map { it.name }
    override fun executionResult(): String = getMethodsNames().toString()
}

class ListDirectoryContents(
    private val currentProjectDirectory: PsiDirectory,
    private val searchDirectoryName: String = DEFAULT_DIRECTORY_NAME
) : IdeApiMethod {
    private lateinit var searchDirectoryItems: List<PsiFileSystemItem>

    private fun PsiDirectory.fileSystemItems(): List<PsiFileSystemItem> {
        val files = this.files.map { it as PsiFileSystemItem }
        val subdirectories = this.subdirectories.map { it as PsiFileSystemItem }
        return files.plus(subdirectories)
    }

    override fun execute() {
        val searchDirectory = when (searchDirectoryName) {
            DEFAULT_DIRECTORY_NAME -> currentProjectDirectory
            else -> currentProjectDirectory.findSubdirectoryRecursively(searchDirectoryName) ?: error("No such subdirectory")
        }
        searchDirectoryItems = searchDirectory.fileSystemItems()
    }

    internal fun getDirContentsNames() = searchDirectoryItems.map { it.name }

    override fun executionResult(): String = getDirContentsNames().toString()
}

class ChangeDirectory(
    private val ideStateKeeper: IdeStateKeeper,
    private val targetDirName: String = DEFAULT_DIRECTORY_NAME
) : ReversibleApiMethod {
    private var prevDir: PsiDirectory? = null

    override fun execute() {
        if (targetDirName == DEFAULT_DIRECTORY_NAME) {
            return
        }

        val targetDir = ideStateKeeper.currentProjectDirectory.findSubdirectoryRecursively(targetDirName)
            ?: error("No such directory in a project: '$targetDirName'.")

        prevDir = ideStateKeeper.currentProjectDirectory
        ideStateKeeper.currentProjectDirectory = targetDir
    }

    override fun executionResult(): String =
        when (targetDirName) {
            DEFAULT_DIRECTORY_NAME -> "Working directory remains the same."
            else -> "Working directory was changed to '$targetDirName'."
        }

    override fun reverse() {
        prevDir?.let {
            ideStateKeeper.currentProjectDirectory = it
            prevDir = null
        }
    }
}

class SaveModelFinalAns(private val modelFinalAns: String) : ReversibleApiMethod {
    override fun execute() {
        TODO("Not yet implemented")
    }

    override fun executionResult(): String {
        TODO("Not yet implemented")
    }

    override fun reverse() {
        TODO("Not yet implemented")
    }
}