package com.delta.mobileplatform.web.controller.media

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import com.delta.mobileplatform.model.response.CommandExecuteResult

import java.io.File

class RecorderController {

    private var recorder: MediaRecorder? = null
    lateinit var curFile: File

    fun startRecord(context: Context) {
        try {
            initRecorder(context)
            recorder?.start()
        } catch (e: Exception) {
            throw CommandExecuteResult.Fail(
                methods = MediaController.methodStartRecord,
                message = e.message.toString()
            )
        }
    }

    fun stopRecord(): File {
        recorder?.let {
            try {
                recorder?.stop()
                return curFile
            }
            catch (e: Exception) {
                throw CommandExecuteResult.Fail(
                    methods = MediaController.methodStopRecord,
                    message = e.message.toString()
                )
            }
        } ?: throw CommandExecuteResult.Fail(
            methods = MediaController.methodStopRecord,
            message = "recorder is null"
        )

    }

    private fun initRecorder(context: Context) {
        recorder = if (Build.VERSION.SDK_INT < 31) MediaRecorder() else MediaRecorder(context)
        recorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)
            setAudioChannels(2)


            curFile = context.getTmpAudioFile()
            setOutputFile(curFile)
            prepare()
        }
    }

}