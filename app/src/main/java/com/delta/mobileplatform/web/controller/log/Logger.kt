package com.delta.mobileplatform.web.controller.log

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.delta.mobileplatform.model.Constant
import com.delta.mobileplatform.model.response.LogFormat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class Logger @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val prettyJson = Json {
        prettyPrint = true
        encodeDefaults = true
    }
    private val fileLock = Any()

    private fun getFileUri() = File(context.getExternalFilesDir("log"), "DeltaLog.txt").apply {
        if (!this.exists()) {
            this.createNewFile()
            this.writeText("[]")
        }
    }.toUri()

    private fun clearFile() = File(context.getExternalFilesDir("log"), "DeltaLog.txt").apply {
        if (this.exists()) {
            this.writeText("")
        }
    }

    fun getLogRecord() = buildString {
        val input = context.contentResolver.openInputStream(getFileUri())
        input?.bufferedReader()?.useLines { lines ->
            lines.forEach(this@buildString::append)
        }
        input?.close()
    }

    fun appendLog(record: LogFormat) {
        synchronized(fileLock) {
            val fileUri = getFileUri()
            val oldRecord: MutableList<LogFormat> = Json.decodeFromString(getLogRecord())
            oldRecord.add(record)
            clearFile()
            val outputStream = context.contentResolver.openOutputStream(fileUri, "wa")
            outputStream?.use {
                it.write(prettyJson.encodeToString(oldRecord).toByteArray())
            }
        }
    }

    fun osLog(body: JsonElement) {
        appendLog(
            LogFormat(
                logType = Constant.OS_LOG_TYPE,
                body = body
            )
        )
    }

    fun removeRecordOlderThanFiveDays() {
        val fiveDaysAgo = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -5)
        }

        val allLog = Json.decodeFromString<List<LogFormat>>(getLogRecord())
        val filteredLog = allLog.filter { it.date >= fiveDaysAgo.time }

        if (filteredLog.isNotEmpty()) {
            clearFile()
            val outputStream = context.contentResolver.openOutputStream(getFileUri())
            outputStream?.use {
                it.write(prettyJson.encodeToString(filteredLog).toByteArray())
            }
        }
    }

}
