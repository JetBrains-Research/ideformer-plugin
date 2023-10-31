package org.jetbrains.research.ideFormerPlugin.api

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil

const val DEFAULT_DIRECTORY_NAME = "."

fun PsiDirectory.findSubdirectoryRecursively(targetDirectoryPath: String): PsiDirectory? {
    val nextDirectoryInPath = targetDirectoryPath.substringBefore("/")
    val currentDirectory = this.findSubdirectory(nextDirectoryInPath) ?: return null
    val remainingDirectoryPath = targetDirectoryPath.substringAfter("/", "")

    return if (remainingDirectoryPath.isNotEmpty()) currentDirectory.findSubdirectoryRecursively(remainingDirectoryPath) else currentDirectory
}

fun PsiDirectory.findFileByName(fileName: String): PsiFile =
    this.findFile(fileName) ?: error("No such file in the current directory")

fun PsiFile.getAllClasses(): List<PsiClass> =
    PsiTreeUtil.getChildrenOfTypeAsList(this, PsiClass::class.java)