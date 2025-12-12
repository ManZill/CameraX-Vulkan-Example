package com.manzill.example.camxvk.core.camera

import android.content.Context
import android.hardware.HardwareBuffer
import android.view.Surface
import android.view.SurfaceHolder
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executors

class VulkanCameraEngine {
    private val surfaceCallback = SurfaceCallback(
        setSurface = ::setSurface
    )

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    var surfaceHolder: SurfaceHolder? = null
        set(value) {
            field?.removeCallback(surfaceCallback)
            field = value
            // field?.setFormat(PixelFormat.RGBA_8888)
            field?.addCallback(surfaceCallback)
        }

    fun startCamera(context: Context, lifecycleOwner: LifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val resolutionSelector = ResolutionSelector.Builder()
                .setResolutionStrategy(ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY)
                .build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                processImage(imageProxy)
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    imageAnalysis
                )
            } catch (exc: Exception) {
                exc.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(context))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImage(imageProxy: ImageProxy) {
        val hardwareBuffer = imageProxy.image?.hardwareBuffer
        if (hardwareBuffer != null) {
            processHardwareBuffer(hardwareBuffer)
            hardwareBuffer.close()
        }
        imageProxy.close()
    }

    private external fun setSurface(
        surface: Surface?,
        width: Int,
        height: Int
    )

    private external fun processHardwareBuffer(buffer: HardwareBuffer)

    private external fun initialize()

    init {
        System.loadLibrary("vulkan-camera")
        initialize()
    }
}
