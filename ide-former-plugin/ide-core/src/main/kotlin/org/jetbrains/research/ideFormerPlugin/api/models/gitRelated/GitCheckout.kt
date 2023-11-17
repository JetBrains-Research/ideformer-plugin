package org.jetbrains.research.ideFormerPlugin.api.models.gitRelated

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.*
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod

class GitCheckout(
    private val project: Project,
    private val projectGitRoot: VirtualFile,
    private val branchName: String
) : ReversibleApiMethod {
    // TODO: to add a current branch for reverse method

    override fun execute() {
        val git = Git.getInstance()

        val checkoutCommandHandler = GitLineHandler(project, projectGitRoot, GitCommand.CHECKOUT)
        checkoutCommandHandler.addParameters(branchName)

        val checkoutCommandResult: GitCommandResult = git.runCommand(checkoutCommandHandler)
        if (!checkoutCommandResult.success()) error(checkoutCommandResult.errorOutputAsJoinedString)
    }

    override fun reverse() {
        TODO("Not yet implemented")
    }
}