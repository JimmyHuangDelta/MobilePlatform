package com.delta.mobileplatform.web.controller.jsCommand.parser

import com.delta.mobileplatform.model.receive.JsCommand

interface JsCommandParser {
    fun parse(command: String): JsCommand
}