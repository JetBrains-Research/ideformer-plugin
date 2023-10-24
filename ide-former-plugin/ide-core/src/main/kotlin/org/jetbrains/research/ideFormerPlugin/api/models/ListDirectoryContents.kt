package org.jetbrains.research.ideFormerPlugin.api.models

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileSystemItem
import org.jetbrains.research.ideFormerPlugin.api.DEFAULT_DIRECTORY_NAME
import org.jetbrains.research.ideFormerPlugin.api.findSubdirectoryRecursively

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
            else -> currentProjectDirectory.findSubdirectoryRecursively(searchDirectoryName)
                ?: error("No such subdirectory")
        }
        searchDirectoryItems = searchDirectory.fileSystemItems()
    }

    fun getSearchDirectoryItemsNames() = searchDirectoryItems.map { it.name }
}