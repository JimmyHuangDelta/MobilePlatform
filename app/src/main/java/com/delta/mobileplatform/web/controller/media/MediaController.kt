package com.delta.mobileplatform.web.controller.media

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.delta.mobileplatform.web.controller.jsCommand.JsonRpcScript
import com.delta.mobileplatform.web.controller.media.remote.MediaAPI
import com.delta.mobileplatform.model.receive.MediaParams
import com.delta.mobileplatform.model.receive.JsCommand
import com.delta.mobileplatform.model.response.CommandExecuteResult
import com.delta.mobileplatform.model.response.toJsResponse
import com.delta.mobileplatform.ui.CameraDialogFragment

import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import java.io.File
import javax.inject.Inject

class MediaController @Inject constructor(
    @ActivityContext val context: Context,
    private val commandQueue: Channel<String>,
    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO),
) : JsonRpcScript {

    private val mediaAPI: MediaAPI by lazy { MediaAPI(context) }

    companion object {
        const val name = "MediaInterface"
        const val methodDownload = "$name.download"
        const val methodTakePhoto = "$name.takePhoto"
        const val methodStopRecord = "$name.stopRecord"
        const val methodStartRecord = "$name.startRecord"
        const val methodRecordVideo = "$name.recordVideo"
        const val methodUploadFiles = "$name.uploadFiles"
        const val methodOpenFileGallery = "$name.openFileGallery"
        const val methodGetServerFileList = "$name.getServerFileList"
    }

    override val methods: Map<String, (JsCommand) -> Unit>
        get() = mapOf(
            methodOpenFileGallery to { openFileGallery(it.params) },
            methodGetServerFileList to { getServerFileList() },
            methodDownload to { download(it.params) },
            methodTakePhoto to { takePhoto(it.params) },
            methodRecordVideo to { recordVideo(it.params) },
            methodStartRecord to { startRecord(it.params) },
            methodStopRecord to { stopRecord(it.params) },
        )

    private val recorderController by lazy { RecorderController() }

    private val activity by lazy { context as AppCompatActivity }

    private lateinit var photoUri: Uri
    private lateinit var photoFile: File

    private lateinit var downloadParams: MediaParams
    private lateinit var openFileParams: MediaParams
    private lateinit var takePhotoParams: MediaParams
    private lateinit var stopRecordParams: MediaParams
    private lateinit var recordVideoParams: MediaParams
    private lateinit var startRecordParams: MediaParams

    private val takePhotoLauncher =
        activity.registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                scope.launch {
                    val res = mediaAPI.uploadFiles(listOf(photoFile), takePhotoParams)
                    Log.d("media", res.toString())
                    val result = CommandExecuteResult.Success(
                        methods = methodTakePhoto,
                        data = res
                    ).toJsResponse()
                    commandQueue.send(result)
                }
            }
        }

    private val openFilesLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val uriList = mutableListOf<Uri>()

            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                //If multiple file selected
                if (data?.clipData != null) {
                    val count = data.clipData?.itemCount ?: 0
                    for (i in 0 until count) {
                        data.clipData?.getItemAt(i)?.uri?.let { uriList.add(it) }
                    }
                }
                //If single file selected
                else if (data?.data != null) {
                    data.data?.let { uriList.add(it) }
                }
            }

            if (uriList.isNotEmpty()) {
                val files = uriList.map { it.toFile(context, methodOpenFileGallery)!! }

                scope.launch {
                    val res = mediaAPI.uploadFiles(files, openFileParams)
                    val json = CommandExecuteResult.Success(
                        methods = methodOpenFileGallery,
                        data = res
                    ).toJsResponse().replace("\\n", "")
                    commandQueue.send(json)
                }
            }
        }

    private fun getOpenFilesIntent(types: Array<String>): Intent {
        return Intent(ACTION_GET_CONTENT).apply {
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            putExtra(Intent.EXTRA_MIME_TYPES, types)
            type = "*/*"
        }
    }

    private fun takePhoto(params: JsonElement?) {
        takePhotoParams = params.toMediaParams(methodTakePhoto)

        photoFile = context.getTmpPhotoFile()
        photoUri = photoFile.toUriByFileProvider(context)
        takePhotoLauncher.launch(photoUri)
    }

    private fun recordVideo(params: JsonElement?) {
        recordVideoParams = params.toMediaParams(methodRecordVideo)

        CameraDialogFragment(mediaAPI, recordVideoParams, commandQueue, scope).show(
            activity.supportFragmentManager,
            "camera"
        )
    }

    private fun openFileGallery(params: JsonElement?) {
        openFileParams = params.toMediaParams(methodOpenFileGallery)
        Log.d(name, openFileParams.toString())

        if (openFileParams.type.isEmpty()) {
            openFilesLauncher.launch(getOpenFilesIntent(arrayOf("*/*")))
        } else {
            val types = openFileParams.type.map { type -> getFileLauncherTypes(type) }
            openFilesLauncher.launch(getOpenFilesIntent(types.toTypedArray()))
        }
    }

    private fun getFileLauncherTypes(type: String): String {
        return when (type) {
            "txt" -> "text/plain"
            "image" -> "image/*"
            "audio" -> "audio/*"
            "video" -> "video/*"
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "zip" -> "application/zip"
            else -> throw CommandExecuteResult.Fail(
                methods = methodOpenFileGallery,
                message = "type $type is invalid"
            )
        }
    }

    private fun startRecord(params: JsonElement?) {
        startRecordParams = params.toMediaParams(methodStartRecord)
        Log.d(name, startRecordParams.toString())

        recorderController.startRecord(context)

        val result = CommandExecuteResult.Success(
            methods = methodStartRecord,
            data = JsonNull
        ).toJsResponse()

        scope.launch { commandQueue.send(result) }
    }

    private fun stopRecord(params: JsonElement?) {
        stopRecordParams = params.toMediaParams(methodStopRecord)

        Log.d(name, stopRecordParams.toString())

        scope.launch {
            val file = recorderController.stopRecord()
            val res = mediaAPI.uploadFiles(listOf(file), stopRecordParams)
            val result = CommandExecuteResult.Success(
                methods = methodStopRecord,
                data = res
            ).toJsResponse().replace("\\n", "")
            commandQueue.send(result)
        }
    }

    private fun getServerFileList() {
    }

    private fun download(params: JsonElement?) {
        downloadParams = params.toMediaParams(methodDownload)
        scope.launch {
            val res = mediaAPI.downloadFiles(downloadParams)
            val result = CommandExecuteResult.Success(
                methods = methodDownload,
                data = res
            ).toJsResponse()

            commandQueue.send(result)
        }
    }
}