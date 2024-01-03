package org.jetbrains.research.ideFormerPlugin.api.models.gitRelated

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.GitCommand
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

    /**
     * Invariant: reverse is always called when the head of the branch is on that particular commit
     **/
    override fun reverse() {
        // drop the last commit
        executeGitCommand(project, projectGitRoot, GitCommand.RESET, listOf("--soft", "HEAD~1"))
    }
}