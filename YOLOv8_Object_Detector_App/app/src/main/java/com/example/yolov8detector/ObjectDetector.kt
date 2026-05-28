package com.example.yolov8detector

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.FloatBuffer

class ObjectDetector(private val context: Context) {

    data class DetectionResult(
        val rect: RectF,
        val classId: Int,
        val className: String,
        val score: Float
    )

    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val session: OrtSession
    private val labels: List<String>

    init {
        val modelBytes = context.assets.open("yolov8n.onnx").readBytes()
        session = env.createSession(modelBytes)
        labels = loadLabels()
    }

    private fun loadLabels(): List<String> {
        val list = ArrayList<String>()
        val reader = BufferedReader(InputStreamReader(context.assets.open("coco.names")))
        var line: String? = reader.readLine()
        while (line != null) {
            if (line.trim().isNotEmpty()) {
                list.add(line.trim())
            }
            line = reader.readLine()
        }
        reader.close()
        return list
    }

    fun detect(bitmap: Bitmap): List<DetectionResult> {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 640, true)

        // Pré-processamento: Cria buffer CHW normalizado (0-1)
        val floatBuffer = FloatBuffer.allocate(1 * 3 * 640 * 640)
        val pixels = IntArray(640 * 640)
        resizedBitmap.getPixels(pixels, 0, 640, 0, 0, 640, 640)

        // R
        for (i in 0 until 640 * 640) {
            val pixel = pixels[i]
            floatBuffer.put(((pixel shr 16) and 0xFF) / 255.0f)
        }
        // G
        for (i in 0 until 640 * 640) {
            val pixel = pixels[i]
            floatBuffer.put(((pixel shr 8) and 0xFF) / 255.0f)
        }
        // B
        for (i in 0 until 640 * 640) {
            val pixel = pixels[i]
            floatBuffer.put((pixel and 0xFF) / 255.0f)
        }
        floatBuffer.rewind()

        val inputName = session.inputNames.iterator().next()
        val inputTensor = OnnxTensor.createTensor(env, floatBuffer, longArrayOf(1, 3, 640, 640))

        val outputs = session.run(mapOf(inputName to inputTensor))
        val outputTensor = outputs[0] as OnnxTensor
        val floatBufferOut = outputTensor.floatBuffer
        val outputData = FloatArray(floatBufferOut.remaining())
        floatBufferOut.get(outputData)

        inputTensor.close()
        outputs.close()

        return parseYOLOv8Output(outputData)
    }

    private fun parseYOLOv8Output(outputData: FloatArray): List<DetectionResult> {
        val candidates = ArrayList<DetectionResult>()
        val numClasses = labels.size
        val numElements = 4 + numClasses
        val numAnchors = 8400

        for (col in 0 until numAnchors) {
            val xCenter = outputData[0 * numAnchors + col]
            val yCenter = outputData[1 * numAnchors + col]
            val w = outputData[2 * numAnchors + col]
            val h = outputData[3 * numAnchors + col]

            var maxScore = 0.0f
            var classId = -1

            for (row in 4 until numElements) {
                val score = outputData[row * numAnchors + col]
                if (score > maxScore) {
                    maxScore = score
                    classId = row - 4
                }
            }

            if (maxScore > 0.50f && classId in labels.indices) {
                val left = xCenter - w / 2.0f
                val top = yCenter - h / 2.0f
                val right = xCenter + w / 2.0f
                val bottom = yCenter + h / 2.0f

                candidates.add(
                    DetectionResult(
                        rect = RectF(left, top, right, bottom),
                        classId = classId,
                        className = labels[classId],
                        score = maxScore
                    )
                )
            }
        }

        return nms(candidates)
    }

    private fun nms(candidates: List<DetectionResult>, iouThreshold: Float = 0.45f): List<DetectionResult> {
        val sorted = candidates.sortedByDescending { it.score }.toMutableList()
        val selected = ArrayList<DetectionResult>()
        while (sorted.isNotEmpty()) {
            val current = sorted.removeAt(0)
            selected.add(current)
            val iterator = sorted.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (boxIoU(current.rect, next.rect) > iouThreshold) {
                    iterator.remove()
                }
            }
        }
        return selected
    }

    private fun boxIoU(a: RectF, b: RectF): Float {
        val left = maxOf(a.left, b.left)
        val top = maxOf(a.top, b.top)
        val right = minOf(a.right, b.right)
        val bottom = minOf(a.bottom, b.bottom)

        if (left >= right || top >= bottom) return 0.0f

        val intersection = (right - left) * (bottom - top)
        val areaA = (a.right - a.left) * (a.bottom - a.top)
        val areaB = (b.right - b.left) * (b.bottom - b.top)
        val union = areaA + areaB - intersection

        return intersection / union
    }

    fun close() {
        session.close()
        env.close()
    }
}