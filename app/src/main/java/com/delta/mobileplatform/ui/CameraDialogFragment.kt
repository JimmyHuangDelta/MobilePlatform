package com.delta.mobileplatform.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.delta.mobileplatform.databinding.FragCameraBinding
import com.delta.mobileplatform.web.controller.media.MediaController
import com.delta.mobileplatform.web.controller.media.getTmpVideoFile
import com.delta.mobileplatform.web.controller.media.remote.MediaAPI
import com.delta.mobileplatform.model.receive.MediaParams
import com.delta.mobileplatform.model.response.CommandExecuteResult
import com.delta.mobileplatform.model.response.toJsResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.File

class CameraDialogFragment(
    private val mediaAPI: MediaAPI,
    private val mediaParams: MediaParams,
    private val commandQueue: Channel<String>,
    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO),
) : BaseDialogFragment<FragCameraBinding>(FragCameraBinding::inflate) {

    private var recording: Recording? = null
    private val recorder: Recorder by lazy {
        Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.HIGHEST)).build()
    }
    private val videoCapture: VideoCapture<Recorder> by lazy { VideoCapture.withOutput(recorder) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCamera(videoCapture)

        binding.videoCaptureBtn.setOnClickListener {
            captureVideo()
        }

    }

    private fun startCamera(useCase: UseCase) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(binding.viewFinder.surfaceProvider) }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, useCase)
            } catch (e: Exception) {
                throw CommandExecuteResult.Fail(
                    methods = MediaController.methodRecordVideo,
                    message = e.message.toString()
                )
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun captureVideo() {
        binding.videoCaptureBtn.isEnabled = false

        recording?.let { curRecording ->
            // Stop the current recording session.
            curRecording.stop()
            recording = null
            return
        }
        startVideoRecording()
    }


    private fun startVideoRecording() {
        val file = requireContext().getTmpVideoFile()
        val fileOutputOptions = FileOutputOptions.Builder(file).build()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw CommandExecuteResult.Fail(
                methods = MediaController.methodRecordVideo,
                message = "need to granted the RECORD_AUDIO permission"
            )
        }
        recording = videoCapture.output
            .prepareRecording(
                requireContext(),
                fileOutputOptions
            )
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(requireContext())) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        setVideoStartState()
                    }

                    is VideoRecordEvent.Finalize -> {
                        setVideoFinalizeState(recordEvent, file)
                    }
                }
            }
    }

    private fun setVideoFinalizeState(recordEvent: VideoRecordEvent.Finalize, file: File) {
        if (!recordEvent.hasError()) {
            uploadVideoFile(file)
            dismiss()
        } else {
            recording?.close()
            recording = null
            dismiss()
            throw CommandExecuteResult.Fail(
                methods = MediaController.methodRecordVideo,
                message = "Video capture ends with error: " + "${recordEvent.error}"
            )
        }
        binding.videoCaptureBtn.apply {
            text = "start capture"
            isEnabled = true
        }
    }

    private fun setVideoStartState() {
        binding.videoCaptureBtn.apply {
            text = "stop capture"
            isEnabled = true
        }
    }


    private fun uploadVideoFile(file: File) {
        scope.launch {
            val res = mediaAPI.uploadFiles(listOf(file), mediaParams)
            val result = CommandExecuteResult.Success(
                methods = MediaController.methodRecordVideo, data = res
            ).toJsResponse().replace("\\n", "")
            commandQueue.send(result)
        }
    }

}
