package org.jetbrains.research.ideFormerPlugin.api.models.gitRelated

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.*
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod

class GitBranch(
    private val project: Project,
    private val projectGitRoot: VirtualFile,
    private val branchName: String
) : ReversibleApiMethod {

    override fun execute() {
        val git = Git.getInstance()

        val branchCommandHandler = GitLineHandler(project, projectGitRoot, GitCommand.BRANCH)
        branchCommandHandler.addParameters(branchName)

        val branchCommandResult: GitCommandResult = git.runCommand(branchCommandHandler)
        if (!branchCommandResult.success()) error(branchCommandResult.errorOutputAsJoinedString)
    }

    override fun reverse() {
        TODO("Not yet implemented")
    }
}