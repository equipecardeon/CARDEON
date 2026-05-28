package com.example.yolov8detector

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var results: List<ObjectDetector.DetectionResult> = emptyList()
    
    private val boxPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }
    
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#80000000")
        style = Paint.Style.FILL
    }

    fun setResults(newResults: List<ObjectDetector.DetectionResult>) {
        results = newResults
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        for (result in results) {
            val rect = result.rect
            
            // Mapeia coordenadas normalizadas do modelo (640x640) para a dimensão real do canvas
            val scaleX = width / 640f
            val scaleY = height / 640f
            
            val left = rect.left * scaleX
            val top = rect.top * scaleY
            val right = rect.right * scaleX
            val bottom = rect.bottom * scaleY

            canvas.drawRect(left, top, right, bottom, boxPaint)

            val text = "${result.className} (${String.format("%.2f", result.score)})"
            val textWidth = textPaint.measureText(text)
            val textHeight = textPaint.textSize
            
            canvas.drawRect(
                left,
                top - textHeight - 10f,
                left + textWidth + 15f,
                top,
                backgroundPaint
            )
            
            canvas.drawText(text, left + 8f, top - 8f, textPaint)
        }
    }
}