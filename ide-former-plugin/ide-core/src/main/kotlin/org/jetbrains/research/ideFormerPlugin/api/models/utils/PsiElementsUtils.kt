package org.jetbrains.research.ideFormerPlugin.api.models.utils

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Document
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import java.util.concurrent.CompletableFuture

const val PATH_DELIMITER = "/"

fun PsiDirectory.findSubdirectoryRecursively(targetDirectoryPath: String): PsiDirectory {
    val currentDirectory = when (val nextDirectoryInPath = targetDirectoryPath.substringBefore(PATH_DELIMITER)) {
        DEFAULT_DIRECTORY_NAME -> this
        else -> this.findSubdirectory(nextDirectoryInPath) ?: error("No such subdirectory: $nextDirectoryInPath")
    }
    val remainingDirectoryPath = targetDirectoryPath.substringAfter(PATH_DELIMITER, "")

    return if (remainingDirectoryPath.isNotEmpty()) currentDirectory.findSubdirectoryRecursively(remainingDirectoryPath) else currentDirectory
}


fun PsiDirectory.findFileRecursively(targetFilePath: String): PsiFile {
    val targetFileDirectory = when (val fileDirectoryPath = targetFilePath.substringBeforeLast(PATH_DELIMITER, "")) {
        "" -> this
        else -> this.findSubdirectoryRecursively(fileDirectoryPath)
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


fun PsiDirectory.createFileByName(fileName: String) : PsiFile {
    val createdFileFuture = CompletableFuture<PsiFile>()

    ApplicationManager.getApplication().let {
        it.invokeAndWait {
             val createdFile = it.runWriteAction<PsiFile> {
                this.createFile(fileName)
            }
            createdFileFuture.complete(createdFile)
        }
    }
    return createdFileFuture.get()
}


fun PsiDirectory.deleteFileByName(fileName: String) {
    ApplicationManager.getApplication().let {
        it.invokeAndWait {
            it.runWriteAction {
                this.findFileRecursively(fileName).delete()
            }
        }
    }
}

fun PsiFile.setText(text: String) {
    ApplicationManager.getApplication().let {
        it.invokeAndWait {
            it.runWriteAction {
                val psiDocumentManager = PsiDocumentManager.getInstance(this.project)
                val document: Document? = psiDocumentManager.getDocument(this)

                if (document != null) {
                    document.setText(text)
                    psiDocumentManager.commitDocument(document)
                }
            }
        }
    }
}

fun createDirectory() {

}


fun deleteDirectory() {

}