package com.ideassistant

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.JTextField

internal class IDEAssistantToolWindowFactory : ToolWindowFactory, DumbAware {
    private class IDEAssistantToolWindow {
        val contentPanel = JPanel()
        private val chatArea = JTextArea(10, 40)
        private val userInputField = JTextField(30)

        init {
            contentPanel.apply {
                add(userInputField)
                add(chatArea)
            }

            userInputField.addActionListener {
                chatArea.append("User: ${userInputField.text}\n")
                chatArea.append("Assistant: This is a model response stub\n\n")
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