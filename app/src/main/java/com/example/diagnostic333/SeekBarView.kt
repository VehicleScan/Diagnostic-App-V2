package com.example.diagnostic333

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.roundToInt

class SeekBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val barPaint = Paint().apply { isAntiAlias = true }
    private val labelPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        textSize = 32f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        color = Color.rgb(255, 191, 0)
    }
    private val unitPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        textSize = 24f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
    }

    private var minValue: Float = 0f
    private var maxValue: Float = 10000f
    private var currentValue: Float = 0f
    private val baseBarHeight = 50f
    private val cornerRadius = 5f
    private var unit: String = "rpm x1000"
    private val rect = RectF()
    private lateinit var chunkColors: List<Int>

    private val numChunks = 25 // Decreased for a more modern, seamless look
    private val chunkWidth = 8f
    private val chunkGap = 2f

    init {
        configure(0f, 10000f, "rpm x1000")
    }

    fun configure(min: Float, max: Float, unitLabel: String) {
        require(min < max) { "minValue must be less than maxValue" }
        minValue = min
        maxValue = max
        unit = unitLabel
        currentValue = minValue
        updateChunkColors()
        invalidate()
    }

    fun updateValue(value: Float) {
        currentValue = value.coerceIn(minValue, maxValue)
        invalidate()
    }

    private fun updateChunkColors() {
        chunkColors = listOf(
            Color.parseColor("#222222"), // Near-black (start)
            Color.parseColor("#444444"), // Dark gray
            Color.parseColor("#888888"), // Medium gray
            Color.parseColor("#FF6A00"), // Neon Orange
            Color.parseColor("#FF3A3A"), // Bright Red
            Color.parseColor("#FF0000")  // Neon Max Red
        )
    }

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        val barY = height / 2
        val barLength = width * 0.85f
        val barStartX = (width - barLength) / 2
        val barEndX = barStartX + barLength
        val progressRatio = (currentValue - minValue) / (maxValue - minValue)
        val progressChunks = (numChunks * progressRatio).coerceIn(0f, numChunks.toFloat()).toInt()
        val progressX = barStartX + (barLength * progressRatio)

        val totalChunkWidth = numChunks * chunkWidth + (numChunks - 1) * chunkGap
        val scaleFactor = barLength / totalChunkWidth
        val scaledChunkWidth = chunkWidth * scaleFactor
        val scaledChunkGap = chunkGap * scaleFactor

        for (i in 0 until progressChunks) {
            val chunkStartX = barStartX + (scaledChunkWidth + scaledChunkGap) * i
            val colorIndex = (i.toFloat() / numChunks * (chunkColors.size - 1)).roundToInt()
            barPaint.color = chunkColors[colorIndex.coerceIn(0, chunkColors.lastIndex)]
            barPaint.style = Paint.Style.FILL
            val rectF = RectF(
                chunkStartX,
                barY - baseBarHeight / 2,
                chunkStartX + scaledChunkWidth,
                barY + baseBarHeight / 2
            )
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, barPaint)
        }

        barPaint.color = Color.argb(50, 100, 100, 100)
        for (i in progressChunks until numChunks) {
            val chunkStartX = barStartX + (scaledChunkWidth + scaledChunkGap) * i
            val rectF = RectF(
                chunkStartX,
                barY - baseBarHeight / 2,
                chunkStartX + scaledChunkWidth,
                barY + baseBarHeight / 2
            )
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, barPaint)
        }

        drawMarkers(canvas, barStartX, barEndX, barY)

        val textYAbove = barY - baseBarHeight - 15f
        drawLabel(canvas, minValue.roundToInt().toString(), barStartX, textYAbove, labelPaint)
        drawLabel(canvas, maxValue.roundToInt().toString(), barEndX, textYAbove, labelPaint)
        drawLabel(canvas, ((minValue + maxValue) / 2).roundToInt().toString(), (barStartX + barEndX) / 2, textYAbove, labelPaint)

        val textYBelow = barY + baseBarHeight + 35f
        drawLabel(canvas, unit, (barStartX + barEndX) / 2, textYBelow, unitPaint)

        drawCurrentValue(canvas, progressX, barY, progressRatio)
    }

    private fun drawMarkers(canvas: Canvas, startX: Float, endX: Float, centerY: Float) {
        barPaint.style = Paint.Style.FILL
        barPaint.color = Color.WHITE
        val positions = floatArrayOf(0.25f, 0.5f, 0.75f)
        positions.forEach { position ->
            val x = startX + (endX - startX) * position
            canvas.drawCircle(x, centerY, 5f, barPaint)
        }
    }

    private fun drawLabel(canvas: Canvas, text: String, x: Float, y: Float, paint: Paint) {
        val shadowPaint = Paint(paint).apply { color = Color.BLACK }
        canvas.drawText(text, x + 2, y + 2, shadowPaint)
        canvas.drawText(text, x, y, paint)
    }

    private fun drawCurrentValue(canvas: Canvas, x: Float, y: Float, ratio: Float) {
        val indicatorSize = 20f
        val path = Path().apply {
            moveTo(x, y - baseBarHeight / 2 - indicatorSize / 2)
            lineTo(x - indicatorSize / 2, y - baseBarHeight / 2)
            lineTo(x + indicatorSize / 2, y - baseBarHeight / 2)
            close()
        }
        barPaint.color = Color.rgb(255, 191, 0)
        barPaint.style = Paint.Style.FILL
        canvas.drawPath(path, barPaint)
        val valueText = currentValue.roundToInt().toString()
        val textY = y - baseBarHeight / 2 - indicatorSize - 10f
        drawLabel(canvas, valueText, x, textY, labelPaint)
    }
}
