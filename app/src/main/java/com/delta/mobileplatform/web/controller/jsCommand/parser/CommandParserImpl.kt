package com.delta.mobileplatform.web.controller.jsCommand.parser

import com.delta.mobileplatform.model.receive.JsCommand
import com.delta.mobileplatform.model.response.CommandExecuteResult
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class CommandParserImpl : JsCommandParser {
    override fun parse(command: String): JsCommand {
        return try {
            Json.decodeFromString(command)
        } catch (e: Exception) {

            throw CommandExecuteResult.Fail(
                methods = "json type mis match",
                message = "json type mis match, please check \n $command"
            )
        }
    }

}

