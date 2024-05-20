package com.delta.mobileplatform.web.controller.jsCommand.executor

import com.delta.mobileplatform.model.ExecuteStatusCode.methodNotFound
import com.delta.mobileplatform.model.receive.JsCommand
import com.delta.mobileplatform.model.response.CommandExecuteResult

abstract class JsCommandExecutor {
    abstract val runnableMethods: Map<String, (JsCommand) -> Unit>
    abstract val similarMethods: Map<String, (JsCommand) -> Unit>
    fun execute(jsCommand: JsCommand) {
        val runnableMethod = runnableMethods[jsCommand.methods]
        if (runnableMethod != null) {
            runnableMethod.invoke(jsCommand)
        } else {
            var similarMethodExecuted = false
            similarMethods.keys.forEach {
                if (jsCommand.methods.startsWith(it)) {
                    similarMethods[it]?.invoke(jsCommand)
                    similarMethodExecuted = true
                    return@forEach
                }
            }

            if (!similarMethodExecuted) {
                throw CommandExecuteResult.Fail(
                    methods = jsCommand.methods,
                    message = "method not found",
                    code = methodNotFound
                )
            }
        }
    }
}