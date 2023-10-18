package org.jetbrains.research.ideFormerPlugin.api

import com.intellij.psi.PsiDirectory

fun PsiDirectory.findSubdirectoryRecursively(targetDirectoryPath: String): PsiDirectory? {
    val nextDirectoryInPath = targetDirectoryPath.substringBefore("/")
    val currentDirectory = this.findSubdirectory(nextDirectoryInPath) ?: return null
    val remainingDirectoryPath = targetDirectoryPath.substringAfter("/", "")

    return if (remainingDirectoryPath.isNotEmpty()) currentDirectory.findSubdirectoryRecursively(remainingDirectoryPath) else currentDirectory
}

const val DEFAULT_DIRECTORY_NAME = "."