package com.example.yolov8detector

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.yolov8detector.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService
    private var objectDetector: ObjectDetector? = null
    private var tts: TextToSpeech? = null
    
    private var lastSpokenText = ""
    private var lastSpokenTime = 0L
    private val ttsCooldown = 3000L // 3 segundos de cooldown entre falas idênticas
    
    private var isProcessing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()
        tts = TextToSpeech(this, this)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Inicializa o modelo de detecção de forma assíncrona para não travar a UI Thread
        CoroutineScope(Dispatchers.IO).launch {
            try {
                objectDetector = ObjectDetector(this@MainActivity)
                withContext(Dispatchers.Main) {
                    binding.tvStatus.text = "Modelo pronto!"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Falha ao carregar o modelo YOLOv8", e)
                withContext(Dispatchers.Main) {
                    binding.tvStatus.text = "Erro ao carregar modelo ONNX!"
                }
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageProxy(imageProxy)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (exc: Exception) {
                Log.e(TAG, "Erro ao vincular componentes da câmera", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processImageProxy(imageProxy: ImageProxy) {
        if (objectDetector == null || isProcessing) {
            imageProxy.close()
            return
        }

        isProcessing = true
        
        CoroutineScope(Dispatchers.Default).launch {
            val bitmap = imageProxy.toBitmap()
            if (bitmap != null) {
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                val rotatedBitmap = if (rotationDegrees != 0) {
                    val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                } else {
                    bitmap
                }

                val detections = objectDetector?.detect(rotatedBitmap) ?: emptyList()

                withContext(Dispatchers.Main) {
                    binding.overlayView.setResults(detections)
                    
                    if (detections.isNotEmpty()) {
                        val topDetection = detections[0]
                        binding.tvStatus.text = "${topDetection.className} (${String.format("%.0f", topDetection.score * 100)}%)"
                        speakObject(topDetection.className)
                    } else {
                        binding.tvStatus.text = "Monitorando..."
                    }
                }
            }
            imageProxy.close()
            isProcessing = false
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun ImageProxy.toBitmap(): Bitmap? {
        val image = this.image ?: return null
        val planes = image.planes
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        
        var nvIndex = ySize
        val uRowStride = planes[1].rowStride
        val uvPixelStride = planes[1].pixelStride
        val vRowStride = planes[2].rowStride

        for (row in 0 until image.height / 2) {
            for (col in 0 until image.width / 2) {
                val uIndex = row * uRowStride + col * uvPixelStride
                val vIndex = row * vRowStride + col * uvPixelStride
                if (vIndex < vSize) nv21[nvIndex++] = vBuffer.get(vIndex)
                if (uIndex < uSize) nv21[nvIndex++] = uBuffer.get(uIndex)
            }
        }

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun speakObject(label: String) {
        val currentTime = System.currentTimeMillis()
        if (label != lastSpokenText || (currentTime - lastSpokenTime) > ttsCooldown) {
            tts?.speak(label, TextToSpeech.QUEUE_FLUSH, null, null)
            lastSpokenText = label
            lastSpokenTime = currentTime
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.getDefault())
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.US)
            }
        } else {
            Log.e(TAG, "Falha ao iniciar Text-To-Speech")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissão de câmera não concedida.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        objectDetector?.close()
        tts?.stop()
        tts?.shutdown()
    }

    companion object {
        private const val TAG = "YOLOv8Detector"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}