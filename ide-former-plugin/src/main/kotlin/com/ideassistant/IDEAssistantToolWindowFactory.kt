package com.ideassistant

import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import javax.swing.*

class IDEAssistantToolWindowFactory : ToolWindowFactory, DumbAware {
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

            userProject.service<IDEServerService>().startServer()
            startDialogue()
        }

        private fun startDialogue() {
            userInputField.addActionListener {
                val userQuery = userInputField.text
                val modelResponse = userProject.service<IDEServerService>().processUserQuery(userQuery)

                invokeLater {
                    chatArea.append("User: $userQuery\n")
                    chatArea.append("Assistant: $modelResponse\n")
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