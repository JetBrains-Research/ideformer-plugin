package com.ideassistant

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.util.PsiTreeUtil
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

class GetAllKtFileKtMethods(private val ktFileName: String) : IDEApiMethod {
    private fun getKtFileByName(ktFileName: String): KtFile =
        ideStateKeeper.curDirectory.files
            .filterIsInstance<KtFile>()
            .firstOrNull { it.name == ktFileName }
            ?: throw Exception("No such file in the current directory")

    companion object {
        fun getKtFileKtMethods(ktFile: KtFile): List<KtNamedFunction> =
            PsiTreeUtil.findChildrenOfType(ktFile, KtNamedFunction::class.java).toList()
    }

    override fun execute(): String = try {
        val ktFile = getKtFileByName(ktFileName)
        getKtFileKtMethods(ktFile)
            .map { it.name }
            .toString()
    } catch (e: Exception) {
        e.message!!
    }
}

class ListDirectories(private val dirName: String = "") : IDEApiMethod {
    companion object {
        fun getListDirectories(psiDirectory: PsiDirectory): List<PsiFileSystemItem> {
            val files = psiDirectory.files.map { it as PsiFileSystemItem }
            val dirs = psiDirectory.subdirectories.map { it as PsiFileSystemItem }
            return files.plus(dirs)
        }
    }

    override fun execute(): String = try {
        val psiDirectory = when (dirName) {
            "" -> ideStateKeeper.curDirectory
            else -> ideStateKeeper.curDirectory.findSubdirectory(dirName) ?: throw Exception("No such subdirectory")
        }
        getListDirectories(psiDirectory)
            .map { it.name }
            .toString()
    } catch (e: Exception) {
        e.message!!
    }
}

class ChangeDirectory(private val targetDirName: String) : IDEApiMethod, ReversibleApiMethod {
    private var prevDir: PsiDirectory? = null
    override fun execute(): String = try {
        // non-recursive search
        val targetDir = ideStateKeeper.curDirectory.findSubdirectory(targetDirName)
            ?: throw Exception("No such directory in a project: $targetDirName")

        prevDir = ideStateKeeper.curDirectory
        ideStateKeeper.curDirectory = targetDir
        "Directory was changed: new dir is '${targetDir.name}'"
    } catch (e: Exception) {
        e.message!!
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