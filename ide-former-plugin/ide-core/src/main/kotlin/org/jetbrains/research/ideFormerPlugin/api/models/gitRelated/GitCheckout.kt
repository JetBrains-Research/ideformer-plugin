package org.jetbrains.research.ideFormerPlugin.api.models.gitRelated

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.*
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod
import org.jetbrains.research.ideFormerPlugin.api.models.utils.executeGitCommand

class GitCheckout(
    private val project: Project,
    private val projectGitRoot: VirtualFile,
    private val branchName: String
) : ReversibleApiMethod {
    private val previousBranchName: String = executeGitCommand(
        project,
        projectGitRoot,
        GitCommand.REV_PARSE,
        listOf("--abbrev-ref", "HEAD")
        )

    override fun execute() {
        executeGitCommand(project, projectGitRoot, GitCommand.CHECKOUT, listOf(branchName))
    }

    override fun reverse() {
        // checkout to the previous branch
        executeGitCommand(project, projectGitRoot, GitCommand.CHECKOUT, listOf(previousBranchName))
    }
}