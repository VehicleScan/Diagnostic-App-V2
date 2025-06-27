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
        color = Color.rgb(255, 191, 0) // Yellowish-orange
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
    private val cornerRadius = 5f // Reduced for thin chunks
    private var unit: String = "rpm x1000"
    private val rect = RectF()
    private lateinit var gradient: LinearGradient
    private val glowPaint = Paint().apply { isAntiAlias = true; maskFilter = BlurMaskFilter(12f, BlurMaskFilter.Blur.NORMAL) }

    private val numChunks = 40 // Increased to span full range
    private val chunkWidth = 8f // Width in dp
    private val chunkGap = 2f // Gap between chunks in dp

    init {
        configure(0f, 10000f, "rpm x1000") // Ensure initial configuration
    }

    fun configure(min: Float, max: Float, unitLabel: String) {
        require(min < max) { "minValue must be less than maxValue" }
        minValue = min
        maxValue = max
        unit = unitLabel
        currentValue = minValue // Reset current value to min on configure
        updateGradient() // Update gradient on configuration
        invalidate()
    }

    fun updateValue(value: Float) {
        currentValue = value.coerceIn(minValue, maxValue)
        updateGradient() // Update gradient with new value
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateGradient()
    }

    private fun updateGradient() {
        val progressRatio = (currentValue - minValue) / (maxValue - minValue)
        val startColor = Color.WHITE // White at the start
        val endColor = Color.RED // Red at the end
        gradient = LinearGradient(0f, 0f, chunkWidth, 0f, intArrayOf(startColor, endColor), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP)
        glowPaint.shader = gradient
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

        // Adjust to span full barLength
        val totalChunkWidth = numChunks * chunkWidth + (numChunks - 1) * chunkGap
        val scaleFactor = barLength / totalChunkWidth
        val scaledChunkWidth = chunkWidth * scaleFactor
        val scaledChunkGap = chunkGap * scaleFactor

        // Draw background track with chunk outlines
        barPaint.color = Color.argb(150, 100, 100, 100)
        barPaint.style = Paint.Style.FILL
        for (i in 0 until numChunks) {
            val chunkStartX = barStartX + (scaledChunkWidth + scaledChunkGap) * i
            val rectF = RectF(
                chunkStartX,
                barY - baseBarHeight / 2,
                chunkStartX + scaledChunkWidth,
                barY + baseBarHeight / 2
            )
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, barPaint)
        }

        // Draw progress chunks with white-to-red gradient
        barPaint.style = Paint.Style.FILL
        for (i in 0 until progressChunks) {
            val chunkStartX = barStartX + (scaledChunkWidth + scaledChunkGap) * i
            val rectF = RectF(
                chunkStartX,
                barY - baseBarHeight / 2,
                chunkStartX + scaledChunkWidth,
                barY + baseBarHeight / 2
            )
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, glowPaint)
        }

        // Draw border for each chunk
        barPaint.style = Paint.Style.STROKE
        barPaint.strokeWidth = 2f
        barPaint.color = Color.WHITE
        for (i in 0 until numChunks) {
            val chunkStartX = barStartX + (scaledChunkWidth + scaledChunkGap) * i
            val rectF = RectF(
                chunkStartX,
                barY - baseBarHeight / 2,
                chunkStartX + scaledChunkWidth,
                barY + baseBarHeight / 2
            )
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, barPaint)
        }

        // Draw markers
        drawMarkers(canvas, barStartX, barEndX, barY)

        // Draw labels above the bar
        val textYAbove = barY - baseBarHeight - 15f
        drawLabel(canvas, minValue.roundToInt().toString(), barStartX, textYAbove, labelPaint)
        drawLabel(canvas, maxValue.roundToInt().toString(), barEndX, textYAbove, labelPaint)
        drawLabel(canvas, ((minValue + maxValue) / 2).roundToInt().toString(), (barStartX + barEndX) / 2, textYAbove, labelPaint)

        // Draw unit label below the bar
        val textYBelow = barY + baseBarHeight + 35f
        drawLabel(canvas, unit, (barStartX + barEndX) / 2, textYBelow, unitPaint)

        // Draw current value indicator
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