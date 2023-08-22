package com.ideassistant

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextField

class IDEAssistantToolWindowFactory : ToolWindowFactory, DumbAware {
    private class IDEAssistantToolWindow {
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

            startDialog()
        }

        private fun startDialog() {
            userInputField.addActionListener {
                chatArea.append("User: ${userInputField.text}\n")
                val modelResponse = LLMSimulator.processUserQuery(
                    userInputField.text,
                    ProjectManager.getInstance().openProjects.first()
                )
                chatArea.append("Assistant: ${modelResponse}\n")
                userInputField.text = ""
            }
        }

    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val ideToolWindow = IDEAssistantToolWindow()
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(ideToolWindow.contentPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}