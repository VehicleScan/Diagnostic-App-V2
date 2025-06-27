package com.example.diagnostic333

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
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
        typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
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
    private val barHeight = 30f
    private val cornerRadius = 15f
    private var unit: String = "rpm x1000"
    private val rect = RectF()
    private val gradientColors = intArrayOf(
        Color.rgb(255, 50, 50),   // Bright red
        Color.rgb(200, 0, 0)       // Dark red
    )
    private val gradientPositions = floatArrayOf(0.0f, 1.0f)
    private lateinit var gradient: LinearGradient

    init {
        configure(0f, 10000f, "rpm x1000")
    }

    fun configure(min: Float, max: Float, unitLabel: String) {
        require(min < max) { "minValue must be less than maxValue" }
        minValue = min
        maxValue = max
        unit = unitLabel
        currentValue = minValue
        invalidate()
    }

    fun updateValue(value: Float) {
        currentValue = value.coerceIn(minValue, maxValue)
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h,oldw, oldh)
        gradient = LinearGradient(
            0f, 0f, width.toFloat(), 0f,
            gradientColors, gradientPositions, Shader.TileMode.CLAMP
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
        val progressX = barStartX + barLength * progressRatio

        // Draw background track
        barPaint.color = Color.argb(150, 100, 100, 100)
        barPaint.style = Paint.Style.FILL
        rect.set(barStartX, barY - barHeight / 2, barEndX, barY + barHeight / 2)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, barPaint)

        // Draw progress with gradient
        barPaint.shader = gradient
        rect.set(barStartX, barY - barHeight / 2, progressX, barY + barHeight / 2)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, barPaint)
        barPaint.shader = null

        // Draw border
        barPaint.style = Paint.Style.STROKE
        barPaint.strokeWidth = 2f
        barPaint.color = Color.WHITE
        rect.set(barStartX, barY - barHeight / 2, barEndX, barY + barHeight / 2)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, barPaint)

        // Draw markers
        drawMarkers(canvas, barStartX, barEndX, barY)

        // Draw labels above the bar
        val textYAbove = barY - barHeight - 15f
        drawLabel(canvas, "${(minValue / 1000).roundToInt()}", barStartX, textYAbove, labelPaint)
        drawLabel(canvas, "${(maxValue / 1000).roundToInt()}", barEndX, textYAbove, labelPaint)
        drawLabel(canvas, "${((minValue + maxValue) / 2000).roundToInt()}",
            (barStartX + barEndX) / 2, textYAbove, labelPaint)

        // Draw unit label below the bar
        val textYBelow = barY + barHeight + 35f
        drawLabel(canvas, unit, (barStartX + barEndX) / 2, textYBelow, unitPaint)

        // Draw current value indicator
        drawCurrentValue(canvas, progressX, barY, progressRatio)
    }

    private fun drawMarkers(canvas: Canvas, startX: Float, endX: Float, centerY: Float) {
        barPaint.style = Paint.Style.FILL
        barPaint.color = Color.WHITE

        // Draw markers at 25%, 50%, 75%
        val positions = floatArrayOf(0.25f, 0.5f, 0.75f)
        positions.forEach { position ->
            val x = startX + (endX - startX) * position
            canvas.drawCircle(x, centerY, 5f, barPaint)
        }
    }

    private fun drawLabel(canvas: Canvas, text: String, x: Float, y: Float, paint: Paint) {
        // Draw text with subtle shadow for better readability
        val shadowPaint = Paint(paint).apply { color = Color.BLACK }
        canvas.drawText(text, x + 2, y + 2, shadowPaint)
        canvas.drawText(text, x, y, paint)
    }

    private fun drawCurrentValue(canvas: Canvas, x: Float, y: Float, ratio: Float) {
        // Draw indicator triangle
        val indicatorSize = 20f
        val path = Path().apply {
            moveTo(x, y - barHeight/2 - indicatorSize/2)
            lineTo(x - indicatorSize/2, y - barHeight/2)
            lineTo(x + indicatorSize/2, y - barHeight/2)
            close()
        }

        barPaint.color = Color.rgb(255, 191, 0)
        barPaint.style = Paint.Style.FILL
        canvas.drawPath(path, barPaint)

        // Draw current value text
        val valueText = "${(currentValue / 1000).roundToInt()}"
        val textY = y - barHeight/2 - indicatorSize - 10f
        drawLabel(canvas, valueText, x, textY, labelPaint)
    }
}