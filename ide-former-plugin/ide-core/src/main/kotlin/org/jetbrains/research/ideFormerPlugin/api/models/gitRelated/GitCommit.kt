package org.jetbrains.research.ideFormerPlugin.api.models.gitRelated

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.*
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod
import org.jetbrains.research.ideFormerPlugin.api.models.utils.executeGitCommand

class GitCommit(
    private val project: Project,
    private val projectGitRoot: VirtualFile,
    private val commitMessage: String
) : ReversibleApiMethod {

    override fun execute() {
        executeGitCommand(project, projectGitRoot, GitCommand.COMMIT, listOf("-m", commitMessage))
    }

    override fun reverse() {
        TODO("Not yet implemented")
        // drop the new commit
    }
}