package org.jetbrains.research.ideFormerPlugin.api.models.gitRelated

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.*
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod

class GitCommit(
    private val project: Project,
    private val projectGitRoot: VirtualFile,
    private val commitMessage: String
) : ReversibleApiMethod {

    override fun execute() {
        val git = Git.getInstance()

        val commitCommandHandler = GitLineHandler(project, projectGitRoot, GitCommand.COMMIT)
        commitCommandHandler.addParameters("-m", commitMessage)

        val commitCommandResult: GitCommandResult = git.runCommand(commitCommandHandler)
        if (!commitCommandResult.success()) error(commitCommandResult.errorOutputAsJoinedString)
    }

    override fun reverse() {
        TODO("Not yet implemented")
        // drop the new commit
    }
}