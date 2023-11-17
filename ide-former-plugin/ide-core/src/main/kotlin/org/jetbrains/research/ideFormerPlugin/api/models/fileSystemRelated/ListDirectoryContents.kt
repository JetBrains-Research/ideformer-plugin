package org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileSystemItem
import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiMethod
import org.jetbrains.research.ideFormerPlugin.api.models.utils.*

class ListDirectoryContents(
    private val currentProjectDirectory: PsiDirectory,
    private val searchDirectoryName: String = DEFAULT_DIRECTORY_NAME
) : IdeApiMethod {
    private var searchDirectoryItems: List<PsiFileSystemItem>? = null

    private fun PsiDirectory.fileSystemItems(): List<PsiFileSystemItem> {
        val files = this.files.map { it as PsiFileSystemItem }
        val subdirectories = this.subdirectories.map { it as PsiFileSystemItem }
        return files.plus(subdirectories)
    }

    override fun execute() {
        val searchDirectory = currentProjectDirectory.findSubdirectoryRecursively(searchDirectoryName)
        searchDirectoryItems = searchDirectory.fileSystemItems()
    }

    fun getSearchDirectoryItemsNames(): List<String> {
        if (searchDirectoryItems == null) error(UNCALLED_EXECUTE_BEFORE_RESULT_GETTING)
        return searchDirectoryItems!!.map { it.name }
    }
}