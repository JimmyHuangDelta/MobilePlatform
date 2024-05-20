package com.delta.mobileplatform.web.controller.media.remote

import android.content.Context
import com.delta.mobileplatform.model.receive.MediaHeader
import com.delta.mobileplatform.model.receive.MediaParams
import com.delta.mobileplatform.model.response.ApiResponse
import com.delta.mobileplatform.model.response.CommandExecuteResult
import com.delta.mobileplatform.model.response.UploadResponse
import com.delta.mobileplatform.web.controller.media.MediaController
import com.delta.mobileplatform.web.controller.media.getTmpDownloadFile
import com.delta.mobileplatform.web.controller.media.saveInFile
import com.delta.mobileplatform.web.controller.media.toMultipartBody

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File

class MediaAPI(private val context: Context) {

    private val client = OkHttpClient()

    companion object {
        private const val TAG = "MediaAPI"
    }

    suspend fun uploadFiles(files: List<File>, params: MediaParams): UploadResponse {

        val body = getUploadBody(files)
        val request = getUploadRequest(params.url, params.header, body)

        try {
            val res = client.newCall(request).execute()
            return res.toUploadResponse()
        } catch (e: Exception) {
            throw CommandExecuteResult.Fail(
                methods = MediaController.methodUploadFiles,
                message = e.message.toString()
            )
        }
    }

    private fun getUploadRequest(url: String, header: MediaHeader, body: RequestBody): Request {
        return Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Accept-Language", header.language)
            .addHeader("Authorization", header.token)
            .build()
    }

    private fun getUploadBody(files: List<File>): RequestBody {
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
        files.forEach { file ->
            val requestBody = file.toMultipartBody()
            body.addPart(requestBody)
        }
        return body.build()
    }

    suspend fun downloadFiles(params: MediaParams): ApiResponse {
        val request = getDownloadRequest(params.ticket, params.url, params.header)

        try {
            val res = client.newCall(request).execute()
            val fileName = res.headers.getFilaName()
            val downloadFile = context.getTmpDownloadFile(fileName)
            saveDownloadFile(downloadFile, res.body)
            return res.toApiResponse()
        } catch (e: Exception) {
            throw CommandExecuteResult.Fail(
                methods = MediaController.methodDownload,
                message = e.message.toString()
            )
        }
    }

    private fun saveDownloadFile(file: File, responseBody: ResponseBody?) {
        responseBody?.saveInFile(file) ?: throw CommandExecuteResult.Fail(
            methods = MediaController.methodDownload,
            message = "save download file fail"
        )
    }

    private fun getDownloadRequest(ticket: String, url: String, header: MediaHeader): Request {
        return Request.Builder()
            .url(url + ticket)
            .addHeader("Accept-Language", header.language)
            .addHeader("Authorization", header.token)
            .build()
    }

}