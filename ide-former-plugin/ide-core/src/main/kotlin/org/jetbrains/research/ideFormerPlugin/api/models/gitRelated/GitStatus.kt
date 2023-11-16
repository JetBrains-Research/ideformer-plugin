package org.jetbrains.research.ideFormerPlugin.api.models.gitRelated

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import git4idea.GitVcs
import git4idea.actions.GitPull
import git4idea.branch.GitBranchUtil
import git4idea.commands.*
import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiMethod

class GitStatus(
    private val project: Project
) : IdeApiMethod {
    private var status: String? = null

    override fun execute() {
        val gitVcs = GitVcs.getInstance(project)

        val gitRoots = GitPull.getGitRoots(project, gitVcs)
        if (gitRoots.isNullOrEmpty()) return

        val selectedRepo = GitBranchUtil.guessRepositoryForOperation(project, DataContext.EMPTY_CONTEXT)
        val defaultRoot = selectedRepo?.root ?: gitRoots[0]

        val git = Git.getInstance()
        val statusCommandResult: GitCommandResult = git.runCommand(GitLineHandler(project, defaultRoot, GitCommand.STATUS))
        status = statusCommandResult.outputAsJoinedString
    }

    // TODO
    fun getStatus(): String = status ?: ""
}