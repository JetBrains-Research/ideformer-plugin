package org.jetbrains.research.ideFormerPlugin.api.models.gitRelated

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.*
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod
import org.jetbrains.research.ideFormerPlugin.api.models.utils.executeGitCommand

class GitAdd(
    private val project: Project,
    private val projectGitRoot: VirtualFile,
    private val fileNames: List<String>
) : ReversibleApiMethod {

    override fun execute() {
        executeGitCommand(project, projectGitRoot, GitCommand.ADD, fileNames)
    }

    override fun reverse() {
        // delete files from the index
        val rmCommandParameters = listOf("--cached") + fileNames
        executeGitCommand(project, projectGitRoot, GitCommand.RM, rmCommandParameters)
    }
}