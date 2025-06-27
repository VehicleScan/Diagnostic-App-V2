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
    private val baseBarHeight = 30f
    private val cornerRadius = 15f
    private var unit: String = "rpm x1000"
    private val rect = RectF()
    private lateinit var gradient: LinearGradient
    private val glowPaint = Paint().apply { isAntiAlias = true; maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.NORMAL) }

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
        super.onSizeChanged(w, h, oldw, oldh)
        updateGradient()
    }

    private fun updateGradient() {
        val progressRatio = (currentValue - minValue) / (maxValue - minValue)
        val startColor = interpolateColor(Color.argb(255, 245, 245, 255), Color.argb(255, 255, 50, 50), progressRatio) // Neon white to neon red
        val endColor = interpolateColor(Color.argb(200, 245, 245, 255), Color.argb(200, 255, 50, 50), progressRatio)
        gradient = LinearGradient(0f, 0f, width.toFloat(), 0f, intArrayOf(startColor, endColor), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP)
        glowPaint.shader = gradient
    }

    private fun interpolateColor(startColor: Int, endColor: Int, fraction: Float): Int {
        val a = (startColor shr 24 and 0xff) + ((endColor shr 24 and 0xff) - (startColor shr 24 and 0xff)) * fraction
        val r = (startColor shr 16 and 0xff) + ((endColor shr 16 and 0xff) - (startColor shr 16 and 0xff)) * fraction
        val g = (startColor shr 8 and 0xff) + ((endColor shr 8 and 0xff) - (startColor shr 8 and 0xff)) * fraction
        val b = (startColor and 0xff) + ((endColor and 0xff) - (startColor and 0xff)) * fraction
        return (a.toInt() shl 24) or (r.toInt() shl 16) or (g.toInt() shl 8) or b.toInt()
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

        // Draw background track with tapered shape
        barPaint.color = Color.argb(150, 100, 100, 100)
        barPaint.style = Paint.Style.FILL
        val path = Path().apply {
            moveTo(barStartX, barY - baseBarHeight / 2)
            lineTo(barStartX + 10f, barY - baseBarHeight / 2) // Narrow start
            lineTo(barEndX - 10f, barY - baseBarHeight / 2 + 10f) // Wider toward end
            lineTo(barEndX, barY - baseBarHeight / 2 + 20f) // Maximum width
            lineTo(barEndX, barY + baseBarHeight / 2 - 20f)
            lineTo(barEndX - 10f, barY + baseBarHeight / 2 - 10f)
            lineTo(barStartX + 10f, barY + baseBarHeight / 2)
            close()
        }
        canvas.drawPath(path, barPaint)

        // Draw progress with tapered shape and neon effect
        barPaint.style = Paint.Style.FILL
        val progressPath = Path().apply {
            moveTo(barStartX, barY - baseBarHeight / 2)
            lineTo(barStartX + 10f * progressRatio, barY - baseBarHeight / 2)
            lineTo(progressX - 10f + 20f * progressRatio, barY - baseBarHeight / 2 + 10f * progressRatio)
            lineTo(progressX, barY - baseBarHeight / 2 + 20f * progressRatio)
            lineTo(progressX, barY + baseBarHeight / 2 - 20f * progressRatio)
            lineTo(progressX - 10f + 20f * progressRatio, barY + baseBarHeight / 2 - 10f * progressRatio)
            lineTo(barStartX + 10f * progressRatio, barY + baseBarHeight / 2)
            close()
        }
        canvas.drawPath(progressPath, glowPaint)

        // Draw border
        barPaint.style = Paint.Style.STROKE
        barPaint.strokeWidth = 2f
        barPaint.color = Color.WHITE
        canvas.drawPath(path, barPaint)

        // Draw markers
        drawMarkers(canvas, barStartX, barEndX, barY)

        // Draw labels above the bar
        val textYAbove = barY - baseBarHeight - 15f
        drawLabel(canvas, "${(minValue / 1000).roundToInt()}", barStartX, textYAbove, labelPaint)
        drawLabel(canvas, "${(maxValue / 1000).roundToInt()}", barEndX, textYAbove, labelPaint)
        drawLabel(canvas, "${((minValue + maxValue) / 2000).roundToInt()}", (barStartX + barEndX) / 2, textYAbove, labelPaint)

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
        val valueText = "${(currentValue / 1000).roundToInt()}"
        val textY = y - baseBarHeight / 2 - indicatorSize - 10f
        drawLabel(canvas, valueText, x, textY, labelPaint)
    }
}