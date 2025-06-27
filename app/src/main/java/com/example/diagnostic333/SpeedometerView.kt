package com.example.diagnostic333

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

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
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }
    private val rect = RectF()
    private var currentValue: Float = 0f
    private var minValue: Float = 0f
    private var maxValue: Float = 220f
    private var needleColor: Int = Color.rgb(139, 69, 19)
    private val startAngle: Float = 90f
    private val sweepAngle: Float = 220f
    private val majorLabels = IntArray(12)

    init {
        configure(0f, 220f, Color.rgb(139, 69, 19))
    }

    fun configure(min: Float, max: Float, color: Int) {
        require(min < max) { "minValue must be less than maxValue" }
        minValue = min
        maxValue = max
        needleColor = color
        updateLabels()
        invalidate()
    }

    fun updateValue(value: Float) {
        currentValue = value.coerceIn(minValue, maxValue)
        invalidate()
    }

    private fun updateLabels() {
        val labelStep = (maxValue - minValue) / (majorLabels.size - 1)
        majorLabels.indices.forEach { i ->
            majorLabels[i] = (minValue + i * labelStep).toInt()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(centerX, centerY)
        val innerRadius = radius

        // Draw background arc
        paint.color = Color.BLACK
        rect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        canvas.drawArc(rect, startAngle, sweepAngle, false, paint)

        // Draw red zone (95 to 100 mph, adjust based on range)
        val redZoneStart = startAngle + ((95f - minValue) / (maxValue - minValue)) * sweepAngle
        val redZoneSweep = (5f / (maxValue - minValue)) * sweepAngle
        paint.color = Color.RED
        canvas.drawArc(rect, redZoneStart, redZoneSweep, false, paint)

        // Draw major ticks and labels
        paint.strokeWidth = 3f
        paint.color = Color.WHITE
        textPaint.color = Color.WHITE
        majorLabels.forEach { value ->
            val angle = startAngle + ((value - minValue) / (maxValue - minValue)) * sweepAngle
            val rad = toRadians(angle.toDouble()).toFloat()

            // Draw tick
            val innerX = centerX + (innerRadius - 20) * cos(rad.toDouble()).toFloat()
            val innerY = centerY + (innerRadius - 20) * sin(rad.toDouble()).toFloat()
            val outerX = centerX + innerRadius * cos(rad.toDouble()).toFloat()
            val outerY = centerY + innerRadius * sin(rad.toDouble()).toFloat()
            canvas.drawLine(innerX, innerY, outerX, outerY, paint)

            // Draw label
            val textRadius = innerRadius - 40
            val textX = centerX + textRadius * cos(rad.toDouble()).toFloat()
            val textY = centerY + textRadius * sin(rad.toDouble()).toFloat() + 12
            canvas.drawText(value.toString(), textX, textY, textPaint)
        }

        // Draw minor ticks
        paint.strokeWidth = 2f
        paint.color = Color.argb(200, 180, 180, 180)
        val minorStep = (maxValue - minValue) / 44
        (minValue.toInt()..maxValue.toInt() step minorStep.toInt()).forEach { value ->
            if (value % ((maxValue - minValue) / (majorLabels.size - 1)).toInt() != 0) {
                val angle = startAngle + ((value - minValue) / (maxValue - minValue)) * sweepAngle
                val rad = toRadians(angle.toDouble()).toFloat()
                val innerX = centerX + (innerRadius - 10) * cos(rad.toDouble()).toFloat()
                val innerY = centerY + (innerRadius - 10) * sin(rad.toDouble()).toFloat()
                val outerX = centerX + innerRadius * cos(rad.toDouble()).toFloat()
                val outerY = centerY + innerRadius * sin(rad.toDouble()).toFloat()
                canvas.drawLine(innerX, innerY, outerX, outerY, paint)
            }
        }

        // Draw needle
        val needleAngle = startAngle + ((currentValue - minValue) / (maxValue - minValue)) * sweepAngle
        val needleRad = toRadians(needleAngle.toDouble()).toFloat()
        val needleLength = innerRadius * 0.9f
        val endX = centerX + needleLength * cos(needleRad.toDouble()).toFloat()
        val endY = centerY + needleLength * sin(needleRad.toDouble()).toFloat()
        paint.strokeWidth = 6f
        paint.color = needleColor
        canvas.drawLine(centerX, centerY, endX, endY, paint)

        // Draw center circle
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, 12f, paint)
    }
}