package org.jetbrains.research.ideFormerPlugin.api.models.gitRelated

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.GitCommand

class GitRm(
    project: Project,
    projectGitRoot: VirtualFile,
    fileNames: List<String>
) : GitApiMethod(project, projectGitRoot) {
    override var gitCommand: GitCommand? = GitCommand.RM
    override var gitCommandParameters: List<String>? = listOf("--cached") + fileNames

    override var gitReverseCommand: GitCommand? = GitCommand.ADD
    override var gitReverseCommandParameters: List<String>? = fileNames
}