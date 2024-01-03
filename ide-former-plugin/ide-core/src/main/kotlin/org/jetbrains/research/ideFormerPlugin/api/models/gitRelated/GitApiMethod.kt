package org.jetbrains.research.ideFormerPlugin.api.models.gitRelated

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.GitCommand
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod
import org.jetbrains.research.ideFormerPlugin.api.models.utils.executeGitCommand

open class GitApiMethod(
    private val project: Project,
    private val projectGitRoot: VirtualFile
) : ReversibleApiMethod {
    open var gitCommand: GitCommand? = null
    open var gitCommandParameters: List<String>? = null

    open var gitReverseCommand: GitCommand? = null
    open var gitReverseCommandParameters: List<String>? = null

    override fun execute() {
        executeGitCommand(
            project,
            projectGitRoot,
            gitCommand!!,
            gitCommandParameters
        )
    }

    /**
     * Reverse -- calling the opposite git command, which cancels the action of the original command
     * If the method is unmodifying (e.g. status), reverse() does nothing
     **/
    override fun reverse() {
        gitReverseCommand?.let {
            executeGitCommand(
                project,
                projectGitRoot,
                it,
                gitReverseCommandParameters
            )
        }
    }
}