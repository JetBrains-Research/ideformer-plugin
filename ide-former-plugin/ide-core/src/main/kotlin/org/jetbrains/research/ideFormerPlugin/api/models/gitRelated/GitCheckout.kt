package org.jetbrains.research.ideFormerPlugin.api.models.gitRelated

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.GitCommand
import org.jetbrains.research.ideFormerPlugin.api.models.utils.executeGitCommand

class GitCheckout(
    project: Project,
    projectGitRoot: VirtualFile,
    branchName: String
) : GitApiMethod(project, projectGitRoot) {
    private val previousBranchName: String = executeGitCommand(
        project,
        projectGitRoot,
        GitCommand.REV_PARSE,
        listOf("--abbrev-ref", "HEAD")
    )

    override var gitCommand: GitCommand? = GitCommand.CHECKOUT
    override var gitCommandParameters: List<String>? = listOf(branchName)

    override var gitReverseCommand: GitCommand? = GitCommand.CHECKOUT
    override var gitReverseCommandParameters: List<String>? = listOf(previousBranchName)
}