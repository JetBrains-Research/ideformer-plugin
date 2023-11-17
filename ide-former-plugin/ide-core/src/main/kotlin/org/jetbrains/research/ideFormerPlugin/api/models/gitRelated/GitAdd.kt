package org.jetbrains.research.ideFormerPlugin.api.models.gitRelated

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.*
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod

class GitAdd(
    private val project: Project,
    private val projectGitRoot: VirtualFile,
    private val fileNames: List<String>
) : ReversibleApiMethod {

    override fun execute() {
        val git = Git.getInstance()

        val addCommandHandler = GitLineHandler(project, projectGitRoot, GitCommand.ADD)
        addCommandHandler.addParameters(fileNames)

        val addCommandResult: GitCommandResult = git.runCommand(addCommandHandler)
        if (!addCommandResult.success()) error(addCommandResult.errorOutputAsJoinedString)
    }

    override fun reverse() {
        TODO("Not yet implemented")
        // delete file from the index
    }
}