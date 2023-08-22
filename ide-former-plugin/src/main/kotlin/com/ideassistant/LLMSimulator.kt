package com.ideassistant

import com.intellij.openapi.project.Project
import kotlin.reflect.full.primaryConstructor

object LLMSimulator {
    private const val RETRIEVED_APIS = "[Retrieved APIs]:"
    private const val CALLED_API = "[Called API]:"
    private const val API_CALL_RES = "[API call res]:"

    fun processUserQuery(userQuery: String, userProject: Project): String {
        val modelActionChain = StringBuilder()

        val retrievedAPIs = retrieveAPINamesByQuery(userQuery)
        modelActionChain.append("$RETRIEVED_APIS $retrievedAPIs\n")

        var prevApiCallRes = ""
        retrievedAPIs.forEach { apiName ->
            modelActionChain.append("$CALLED_API $apiName\n")
            prevApiCallRes = callApi(apiName, prevApiCallRes, userProject)
            modelActionChain.append("$API_CALL_RES $prevApiCallRes\n")
        }

        return modelActionChain.toString()
    }

    // TODO: Now this is a stub that always returns the same API name list
    private fun retrieveAPINamesByQuery(userQuery: String): List<String> {
        return arrayListOf(
            "GetAllProjectModules",
            "GetAllProjectKtMethods"
        )
    }

    /**
     * Current assumption: to generate arguments for a new API call,
     * the model only needs the result of the previous API call
     */
    private fun callApi(apiName: String, prevApiCallRes: String, userProject: Project): String {
        // TODO: this is a stub argument for an API Method instance creating. This arg(s) should be generated by the LLM
        val args = userProject
        val apiMethod: IDEApiMethod = apiNameToApiMethodClass[apiName]?.primaryConstructor!!.call(args)
        return apiMethod.call()
    }
}