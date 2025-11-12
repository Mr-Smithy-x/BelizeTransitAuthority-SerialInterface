package bz.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bytedeco.javacv.FrameGrabber
import org.bytedeco.javacv.OpenCVFrameGrabber
import org.bytedeco.javacv.Java2DFrameConverter
import java.awt.image.BufferedImage

@Composable
fun CameraViewJavaCV(
    modifier: Modifier = Modifier,
    cameraIndex: Int = 0
) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var grabber by remember { mutableStateOf<FrameGrabber?>(null) }

    DisposableEffect(cameraIndex) {
        val frameGrabber = OpenCVFrameGrabber(cameraIndex)
        frameGrabber.start()
        grabber = frameGrabber

        onDispose {
            grabber?.stop()
            grabber?.release()
        }
    }

    LaunchedEffect(grabber) {
        val converter = Java2DFrameConverter()
        grabber?.let { fg ->
            while (true) {
                withContext(Dispatchers.IO) {
                    val frame = fg.grab()
                    frame?.let {
                        if (it.image == null) return@withContext
                        val bufferedImage: BufferedImage = converter.convert(it)
                        imageBitmap = bufferedImage.toComposeImageBitmap()
                    }
                }
            }
        }
    }

    Box(modifier = modifier) {
        imageBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap,
                contentDescription = "Camera feed",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}