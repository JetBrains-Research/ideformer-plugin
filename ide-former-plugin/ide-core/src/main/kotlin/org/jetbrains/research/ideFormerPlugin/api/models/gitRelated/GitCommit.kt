package org.jetbrains.research.ideFormerPlugin.api.models.gitRelated

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.GitCommand

class GitCommit(
    project: Project,
    projectGitRoot: VirtualFile,
    commitMessage: String
) : GitApiMethod(project, projectGitRoot) {
    override var gitCommand: GitCommand? = GitCommand.COMMIT
    override var gitCommandParameters: List<String>? = listOf("-m", commitMessage)

    // Invariant: reverse is always called when the head of the branch is on that particular commit
    override var gitReverseCommand: GitCommand? = GitCommand.RESET
    override var gitReverseCommandParameters: List<String>? = listOf("--soft", "HEAD~1")
}