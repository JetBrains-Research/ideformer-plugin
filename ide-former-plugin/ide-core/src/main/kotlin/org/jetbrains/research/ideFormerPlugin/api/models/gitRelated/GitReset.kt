package org.jetbrains.research.ideFormerPlugin.api.models.gitRelated

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.GitCommand

class GitReset(
    project: Project,
    projectGitRoot: VirtualFile,
    commitsCount: Int
): GitApiMethod(project, projectGitRoot) {
    override var gitCommand: GitCommand? = GitCommand.RESET
    override var gitCommandParameters: List<String>? = listOf("HEAD~$commitsCount")

    override var gitReverseCommand: GitCommand? = GitCommand.RESET
    override var gitReverseCommandParameters: List<String>? = listOf("HEAD@{1}")
}