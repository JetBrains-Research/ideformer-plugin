package org.jetbrains.research.ideFormerPlugin.api.models.utils

import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil

const val PATH_DELIMITER = "/"

fun PsiDirectory.findSubdirectoryRecursively(targetDirectoryPath: String): PsiDirectory? {
    val nextDirectoryInPath = targetDirectoryPath.substringBefore(PATH_DELIMITER)
    val currentDirectory = this.findSubdirectory(nextDirectoryInPath) ?: return null
    val remainingDirectoryPath = targetDirectoryPath.substringAfter(PATH_DELIMITER, "")

    return if (remainingDirectoryPath.isNotEmpty()) currentDirectory.findSubdirectoryRecursively(remainingDirectoryPath) else currentDirectory
}

fun PsiDirectory.findFileRecursively(targetFilePath: String): PsiFile {
    val targetFileDirectory = when (
        val fileDirectoryPath = targetFilePath.substringBeforeLast(PATH_DELIMITER, "")
    ) {
        "" -> this
        else -> this.findSubdirectoryRecursively(fileDirectoryPath) ?: error("No such file directory")
    }

    val fileName = targetFilePath.substringAfterLast(PATH_DELIMITER)
    return targetFileDirectory.findFile(fileName)
        ?: error("No such file in the current directory")
}

inline fun <reified T : PsiElement> PsiFile.psiElementsOfType(): List<T> =
    PsiTreeUtil.findChildrenOfType(this, T::class.java).toList()

inline fun <reified T : PsiElement> getFilePsiElementsOfType(
    projectDirectory: PsiDirectory,
    fileName: String
): List<T> {
    val psiFile = projectDirectory.findFileRecursively(fileName)
    return psiFile.psiElementsOfType<T>()
}