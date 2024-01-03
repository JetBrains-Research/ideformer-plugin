package org.jetbrains.research.ideFormerPlugin.api.models.gitRelated

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.GitCommand

class GitBranch(
    project: Project,
    projectGitRoot: VirtualFile,
    branchName: String
) : GitApiMethod(project, projectGitRoot) {
    override var gitCommand: GitCommand? = GitCommand.BRANCH
    override var gitCommandParameters: List<String>? = listOf(branchName)

    override var gitReverseCommand: GitCommand? = GitCommand.BRANCH
    override var gitReverseCommandParameters: List<String>? = listOf("--delete", branchName)
}