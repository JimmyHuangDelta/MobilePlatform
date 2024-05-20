package com.delta.mobileplatform.web.controller.media

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import androidx.core.content.FileProvider
import com.delta.mobileplatform.model.receive.MediaParams
import com.delta.mobileplatform.model.response.CommandExecuteResult
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream

const val FILENAME_PREFIX_VIDEO = "VIDEO_"
const val FILENAME_PREFIX_PHOTO = "PHOTO_"
const val FILENAME_PREFIX_AUDIO = "AUDIO_"

const val FILENAME_SUFFIX_VIDEO = ".mp4"
const val FILENAME_SUFFIX_PHOTO = ".png"
const val FILENAME_SUFFIX_AUDIO = ".3gp"


fun Context.getTmpVideoFile(): File {
    return File.createTempFile(
        FILENAME_PREFIX_VIDEO,
        FILENAME_SUFFIX_VIDEO,
        this.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
    )
}

fun Context.getTmpPhotoFile(): File {
    return File.createTempFile(
        FILENAME_PREFIX_PHOTO,
        FILENAME_SUFFIX_PHOTO,
        this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    )
}

fun Context.getTmpAudioFile(): File {
    return File.createTempFile(
        FILENAME_PREFIX_AUDIO,
        FILENAME_SUFFIX_AUDIO,
        this.getExternalFilesDir("audio")
    )
}

fun Context.getTmpDownloadFile(filaName: String): File {
    return File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filaName)
}

fun File.toUriByFileProvider(context: Context): Uri {
    return FileProvider.getUriForFile(
        context, "${context.packageName}.provider", this
    )
}

fun File.toMultipartBody(): MultipartBody.Part {
    val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), this)
    return MultipartBody.Part.createFormData("files", this.name, requestBody)
}

fun ResponseBody.saveInFile(file: File) {
    try {
        val inputStream = this.byteStream()
        val outputStream = FileOutputStream(file)
        val buffer = ByteArray(4096)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        outputStream.flush()
        outputStream.close()
        inputStream.close()
    } catch (e: IOException) {
        Log.d("download", e.message.toString())
    }
}

fun ResponseBody.saveAsTextFile(file: File) {
    try {
        val base64 = Base64.decode(this.bytes(), Base64.DEFAULT)
        val outputStream = FileOutputStream(file)
        outputStream.write(base64)
        outputStream.close()
    } catch (e: IOException) {
        Log.d("download", e.message.toString())
    }
}

fun Uri.toFile(context: Context, method: String): File? {
    val cursor = context.contentResolver.query(this, null, null, null, null)
    return cursor?.use {
        it.moveToFirst()
        val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (displayNameIndex != -1) {
            val fileName = it.getString(displayNameIndex)
            val newFile = File(context.cacheDir, fileName)
            val inputStream = context.contentResolver.openInputStream(this)
            newFile.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            inputStream?.close()
            newFile
        } else {
            throw CommandExecuteResult.Fail(
                methods = method,
                message = "uri to File error"
            )
        }
    }
}

fun JsonElement?.toMediaParams(method: String): MediaParams {
    return this?.let {
        Json.decodeFromJsonElement(this)
    } ?: throw CommandExecuteResult.Fail(
        methods = method,
        message = "params is invalid"
    )
}

