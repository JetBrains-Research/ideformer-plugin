package com.ideassistant

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*

class UserQueryTransmitter(private val modelServerUrl: String = "localhost:8081/post") {
    suspend fun sendUserQueryToModel(userQuery: String) {
        val client = HttpClient(CIO)
        val response = client.post(modelServerUrl) {
            setBody(userQuery)
        }

        // TODO: add logging
        println("Response status: $response.status")
        client.close()
    }

    fun serverClientInteractionStub(userQuery: String, userProject: Project): String {
        val interactionChain = StringBuilder()
        val ideApiExecutorService = userProject.service<IDEApiExecutorService>()

        var prevStepInfo = userQuery
        while (true) {
            val modelAPIMethodQuery: IDEApiMethod = LLMSimulator.getAPIQuery(prevStepInfo) ?: break
            interactionChain.append("[API Call Info]: $modelAPIMethodQuery\n")

            val apiCallRes = ideApiExecutorService.executeApiMethod(modelAPIMethodQuery)
            interactionChain.append("[API Call Res]: $apiCallRes\n")

            prevStepInfo = apiCallRes
        }

        return interactionChain.toString()
    }
}

@Service
class UserQueryTransmitterService {
    private val transmitter = UserQueryTransmitter()

    suspend fun sendUserQueryToModel(userQuery: String) {
        transmitter.sendUserQueryToModel(userQuery)
    }

    fun serverClientInteractionStub(userQuery: String, userProject: Project): String =
        transmitter.serverClientInteractionStub(userQuery, userProject)
}