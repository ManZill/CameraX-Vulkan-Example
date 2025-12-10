package com.manzill.example.camxvk.core.camera

import android.view.Surface
import android.view.SurfaceHolder

internal class SurfaceCallback(
    private val setSurface: (
        holder: Surface?,
        width: Int,
        height: Int
    ) -> Unit
) : SurfaceHolder.Callback {
    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ) {
        setSurface(holder.surface, width, height)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        setSurface(null, 0, 0)
    }
}
