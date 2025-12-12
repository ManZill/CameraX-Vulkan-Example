package com.manzill.example.camxvk

import android.Manifest
import android.content.pm.PackageManager
import android.view.SurfaceView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.manzill.example.camxvk.core.camera.VulkanCameraEngine

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraEngine = remember {
        VulkanCameraEngine()
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraEngine.startCamera(context, lifecycleOwner)
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraEngine.startCamera(context, lifecycleOwner)
        } else {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    AndroidView(
        modifier = modifier
            .fillMaxSize(),
        factory = { ctx ->
            SurfaceView(ctx).apply {
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
