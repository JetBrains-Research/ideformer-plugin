package org.jetbrains.research.ideFormerPlugin.api

import com.intellij.psi.PsiDirectory

object PsiElementsUtils {
    fun findSubdirectory(directory: PsiDirectory, targetDirectoryPath: String): PsiDirectory? {
        val nextDirectoryInPath = targetDirectoryPath.substringBefore("/")
        val currentDirectory = directory.findSubdirectory(nextDirectoryInPath) ?: return null
        val remainingDirectoryPath = targetDirectoryPath.substringAfter("/", "")

        return if (remainingDirectoryPath.isNotEmpty()) findSubdirectory(currentDirectory, remainingDirectoryPath) else currentDirectory
    }
}