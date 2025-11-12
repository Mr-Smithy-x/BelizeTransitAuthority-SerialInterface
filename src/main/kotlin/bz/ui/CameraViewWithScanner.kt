package bz.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import com.google.zxing.*
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bytedeco.javacv.FrameGrabber
import org.bytedeco.javacv.Java2DFrameConverter
import org.bytedeco.javacv.OpenCVFrameGrabber
import java.awt.image.BufferedImage

@Composable
fun CameraViewWithScanner(
    modifier: Modifier = Modifier,
    cameraIndex: Int = 0,
    onCodeScanned: (String, BarcodeFormat) -> Unit = { _, _ -> }
) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var grabber by remember { mutableStateOf<FrameGrabber?>(null) }
    var scannedCode by remember { mutableStateOf<String?>(null) }
    var codeFormat by remember { mutableStateOf<BarcodeFormat?>(null) }

    // ZXing reader configuration
    val reader = remember {
        MultiFormatReader().apply {
            val hints = mapOf(
                DecodeHintType.POSSIBLE_FORMATS to listOf(
                    BarcodeFormat.QR_CODE,
                    BarcodeFormat.EAN_13,
                    BarcodeFormat.EAN_8,
                    BarcodeFormat.CODE_128,
                    BarcodeFormat.CODE_39,
                    BarcodeFormat.UPC_A,
                    BarcodeFormat.UPC_E,
                    BarcodeFormat.DATA_MATRIX,
                    BarcodeFormat.PDF_417
                ),
                DecodeHintType.TRY_HARDER to true
            )
            setHints(hints)
        }
    }

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

                        // Try to scan QR/Barcode
                        try {
                            val source = BufferedImageLuminanceSource(bufferedImage)
                            val bitmap = BinaryBitmap(HybridBinarizer(source))
                            val result = reader.decode(bitmap)

                            if (scannedCode != result.text) {
                                scannedCode = result.text
                                codeFormat = result.barcodeFormat
                                withContext(Dispatchers.IO) {
                                    onCodeScanned(result.text, result.barcodeFormat)
                                }
                            }
                        } catch (e: NotFoundException) {
                            // No barcode found in this frame
                        } catch (e: Exception) {
                            // Other decoding errors
                        } finally {
                            reader.reset()
                        }
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

        // Display scanned code
        scannedCode?.let { code ->
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Format: ${codeFormat?.name ?: "Unknown"}",
                    color = Color.White
                )
                Text(
                    text = "Code: $code",
                    color = Color.White
                )
            }
        }
    }
}


@Composable
fun CameraViewWithScanner2(
    modifier: Modifier = Modifier,
    cameraIndex: Int = 0,
    onCodeScanned: (String, BarcodeFormat) -> Unit = { _, _ -> }
) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var grabber by remember { mutableStateOf<FrameGrabber?>(null) }
    var scannedCode by remember { mutableStateOf<String?>(null) }
    var codeFormat by remember { mutableStateOf<BarcodeFormat?>(null) }

    // ZXing reader configuration
    val reader = remember {
        MultiFormatReader().apply {
            val hints = mapOf(
                DecodeHintType.POSSIBLE_FORMATS to listOf(
                    BarcodeFormat.QR_CODE,
                    BarcodeFormat.EAN_13,
                    BarcodeFormat.EAN_8,
                    BarcodeFormat.CODE_128,
                    BarcodeFormat.CODE_39,
                    BarcodeFormat.UPC_A,
                    BarcodeFormat.UPC_E,
                    BarcodeFormat.DATA_MATRIX,
                    BarcodeFormat.PDF_417
                ),
                DecodeHintType.TRY_HARDER to true
            )
            setHints(hints)
        }
    }

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

                        // Try to scan QR/Barcode
                        try {
                            val source = BufferedImageLuminanceSource(bufferedImage)
                            val bitmap = BinaryBitmap(HybridBinarizer(source))
                            val result = reader.decode(bitmap)

                            if (scannedCode != result.text) {
                                scannedCode = result.text
                                codeFormat = result.barcodeFormat
                                withContext(Dispatchers.IO) {
                                    onCodeScanned(result.text, result.barcodeFormat)
                                }
                            }
                        } catch (e: NotFoundException) {
                            // No barcode found in this frame
                        } catch (e: Exception) {
                            // Other decoding errors
                        } finally {
                            reader.reset()
                        }
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

        // Display scanned code
        scannedCode?.let { code ->
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Format: ${codeFormat?.name ?: "Unknown"}",
                    color = Color.White
                )
                Text(
                    text = "Code: $code",
                    color = Color.White
                )
            }
        }
    }
}