package org.jetbrains.research.ideFormerPlugin.api.models.gitRelated

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.*
import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiMethod

class GitStatus(
    private val project: Project,
    private val projectGitRoot: VirtualFile
) : IdeApiMethod {
    private var status: String? = null

    override fun execute() {
        val git = Git.getInstance()
        val statusCommandResult: GitCommandResult = git.runCommand(
            GitLineHandler(project, projectGitRoot, GitCommand.STATUS)
        )
        status = statusCommandResult.outputAsJoinedString
    }

    // TODO
    fun getStatus(): String = status ?: ""
}