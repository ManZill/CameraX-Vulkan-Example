package com.manzill.example.camxvk.core.camera

import android.graphics.PixelFormat
import android.view.Surface
import android.view.SurfaceHolder

class VulkanCameraEngine {
    private val surfaceCallback = SurfaceCallback(
        setSurface = ::setSurface
    )

    var surfaceHolder: SurfaceHolder? = null
        set(value) {
            field?.removeCallback(surfaceCallback)
            field = value
            field?.setFormat(PixelFormat.RGBA_8888) // ToDo: test
            field?.addCallback(surfaceCallback)
        }

    private external fun setSurface(
        surface: Surface?,
        width: Int,
        height: Int
    )

    private external fun initialize()

    init {
        System.loadLibrary("vulkan-camera")
        initialize()
    }
}
