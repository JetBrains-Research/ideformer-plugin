package org.jetbrains.research.ideFormerPlugin.api.models.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.*

fun executeGitCommand(
    project: Project,
    projectGitRoot: VirtualFile,
    gitCommand: GitCommand,
    gitCommandParameters: List<String>?
) : String {
    val git = Git.getInstance()

    val gitCommandHandler = GitLineHandler(project, projectGitRoot, gitCommand)
    gitCommandParameters?.let { gitCommandHandler.addParameters(it) }

    val gitCommandResult: GitCommandResult = git.runCommand(gitCommandHandler)
    if (!gitCommandResult.success()) error(gitCommandResult.errorOutputAsJoinedString)

    return gitCommandResult.outputAsJoinedString
}