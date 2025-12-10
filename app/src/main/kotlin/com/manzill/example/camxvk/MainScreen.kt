package com.manzill.example.camxvk

import android.view.SurfaceView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.manzill.example.camxvk.core.camera.VulkanCameraEngine

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val cameraEngine = remember {
        VulkanCameraEngine()
    }

    AndroidView(
        modifier = modifier
            .fillMaxSize(),
        factory = { context ->
            SurfaceView(context).apply {
                cameraEngine.surfaceHolder = holder
            }
        }
    )
}

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen()
}
