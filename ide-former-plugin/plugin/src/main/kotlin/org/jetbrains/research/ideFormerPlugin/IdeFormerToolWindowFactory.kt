package org.jetbrains.research.ideFormerPlugin

import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import kotlinx.coroutines.runBlocking
import org.jetbrains.research.ideFormerPlugin.queryTransmitter.UserQueryTransmitterService
import org.jetbrains.research.ideFormerPlugin.server.IdeServerService
import javax.swing.*

class IdeFormerToolWindowFactory : ToolWindowFactory, DumbAware {
    private class IDEAssistantToolWindow(private val userProject: Project) {
        val contentPanel = JPanel()
        private val chatArea = JTextArea(10, 50).apply {
            lineWrap = true
            wrapStyleWord = true
            isEditable = false
        }

        // TODO: change to JTextArea
        private val userInputField = JTextField(30)

        init {
            contentPanel.apply {
                add(JLabel("User input:"))
                add(JScrollPane(userInputField))
                add(JScrollPane(chatArea))
            }

            // TODO: to think about server host and port
            userProject.service<IdeServerService>().startServer()
            startDialogue()
        }

        private fun startDialogue() {
            userInputField.addActionListener {
                val userQuery = userInputField.text
                val modelResponse = runBlocking {
                    service<UserQueryTransmitterService>().serverClientInteractionStub(userProject)
                }

                invokeLater {
                    chatArea.append("User: $userQuery${System.lineSeparator()}")
                    chatArea.append("Assistant: $modelResponse${System.lineSeparator()}")
                    userInputField.text = ""
                }
            }
        }
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val ideToolWindow = IDEAssistantToolWindow(project)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(ideToolWindow.contentPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}