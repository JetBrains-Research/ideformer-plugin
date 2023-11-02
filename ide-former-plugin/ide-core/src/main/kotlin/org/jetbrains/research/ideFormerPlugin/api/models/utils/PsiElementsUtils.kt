package org.jetbrains.research.ideFormerPlugin.api.models.utils

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

const val DEFAULT_DIRECTORY_NAME = "."

fun PsiDirectory.findSubdirectoryRecursively(targetDirectoryPath: String): PsiDirectory? {
    val nextDirectoryInPath = targetDirectoryPath.substringBefore("/")
    val currentDirectory = this.findSubdirectory(nextDirectoryInPath) ?: return null
    val remainingDirectoryPath = targetDirectoryPath.substringAfter("/", "")

    return if (remainingDirectoryPath.isNotEmpty()) currentDirectory.findSubdirectoryRecursively(remainingDirectoryPath) else currentDirectory
}

fun PsiDirectory.findFileByName(fileName: String): PsiFile =
    this.findFile(fileName) ?: error("No such file in the current directory")