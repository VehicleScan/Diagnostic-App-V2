package com.example.diagnostic333

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.*

class SpeedometerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 24f
    }

    private val textPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        textSize = 32f
        typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }

    private val rect = RectF()
    private var currentValue: Float = 0f
    private var minValue: Float = 0f
    private var maxValue: Float = 220f
    private var needleColor: Int = Color.rgb(139, 69, 19)
    private val startAngle: Float = 90f
    private val sweepAngle: Float = 230f
    private val majorLabels = IntArray(12)
    private var yellowStart: Float = 90f + 0.70f * 230f
    private var yellowEnd: Float = 90f + 0.85f * 230f
    private var redStart: Float = 90f + 0.85f * 230f
    private var redEnd: Float = 90f + 1f * 230f

    init {
        configure(minValue, maxValue, needleColor, yellowStart, yellowEnd, redStart, redEnd)
    }

    fun configure(min: Float, max: Float, color: Int, yellowStartAngle: Float = 90f + 0.70f * 230f, yellowEndAngle: Float = 90f + 0.85f * 230f, redStartAngle: Float = 90f + 0.85f * 230f, redEndAngle: Float = 90f + 1f * 230f) {
        require(min < max) { "minValue must be less than maxValue" }
        minValue = min
        maxValue = max
        needleColor = color
        yellowStart = yellowStartAngle
        yellowEnd = yellowEndAngle
        redStart = redStartAngle
        redEnd = redEndAngle
        updateLabels()
        invalidate()
    }

    fun updateValue(value: Float) {
        currentValue = value.coerceIn(minValue, maxValue)
        invalidate()
    }

    private fun updateLabels() {
        val labelStep = (maxValue - minValue) / (majorLabels.size - 1)
        for (i in majorLabels.indices) {
            majorLabels[i] = (minValue + i * labelStep).toInt()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(centerX, centerY)
        val innerRadius = radius

        rect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        // Background arc
        paint.color = Color.BLACK
        canvas.drawArc(rect, startAngle, sweepAngle, false, paint)

        paint.strokeWidth = 24f

        // Draw yellow arc
        paint.color = Color.YELLOW
        canvas.drawArc(rect, yellowStart, yellowEnd - yellowStart, false, paint)

        // Draw red arc
        paint.color = Color.RED
        canvas.drawArc(rect, redStart, redEnd - redStart, false, paint)

        // Major ticks and labels
        paint.strokeWidth = 3f
        paint.color = Color.WHITE
        textPaint.color = Color.WHITE
        for (value in majorLabels) {
            val angle = startAngle + ((value - minValue) / (maxValue - minValue)) * sweepAngle
            val rad = Math.toRadians(angle.toDouble()).toFloat()

            val innerX = centerX + (innerRadius - 20) * cos(rad)
            val innerY = centerY + (innerRadius - 20) * sin(rad)
            val outerX = centerX + innerRadius * cos(rad)
            val outerY = centerY + innerRadius * sin(rad)
            canvas.drawLine(innerX, innerY, outerX, outerY, paint)

            val textRadius = innerRadius - 40
            val textX = centerX + textRadius * cos(rad)
            val textY = centerY + textRadius * sin(rad) + 12
            canvas.drawText(value.toString(), textX, textY, textPaint)
        }

        // Minor ticks
        paint.strokeWidth = 2f
        paint.color = Color.argb(200, 180, 180, 180)
        val minorStep = (maxValue - minValue) / 44
        for (value in minValue.toInt()..maxValue.toInt() step minorStep.toInt()) {
            if (value % ((maxValue - minValue) / (majorLabels.size - 1)).toInt() != 0) {
                val angle = startAngle + ((value - minValue) / (maxValue - minValue)) * sweepAngle
                val rad = Math.toRadians(angle.toDouble()).toFloat()
                val innerX = centerX + (innerRadius - 10) * cos(rad)
                val innerY = centerY + (innerRadius - 10) * sin(rad)
                val outerX = centerX + innerRadius * cos(rad)
                val outerY = centerY + innerRadius * sin(rad)
                canvas.drawLine(innerX, innerY, outerX, outerY, paint)
            }
        }

        // Needle
        val needleAngle = startAngle + ((currentValue - minValue) / (maxValue - minValue)) * sweepAngle
        val needleRad = Math.toRadians(needleAngle.toDouble()).toFloat()
        val needleLength = innerRadius * 0.9f
        val endX = centerX + needleLength * cos(needleRad)
        val endY = centerY + needleLength * sin(needleRad)
        paint.strokeWidth = 6f
        paint.color = needleColor
        canvas.drawLine(centerX, centerY, endX, endY, paint)

        // Center circle
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, 12f, paint)
    }
}