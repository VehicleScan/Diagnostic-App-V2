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

    private val paint = Paint().apply { isAntiAlias = true }
    private val textPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        textSize = 24f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        color = Color.rgb(255, 191, 0) // Yellowish-orange
    }
    private var minValue: Float = 0f
    private var maxValue: Float = 10000f
    private var currentValue: Float = 0f
    private val barHeight = 20f
    private val rect = RectF()

    init {
        configure(0f, 10000f)
    }

    fun configure(min: Float, max: Float) {
        require(min < max) { "minValue must be less than maxValue" }
        minValue = min
        maxValue = max
        currentValue = minValue
        invalidate()
    }

    fun updateValue(value: Float) {
        currentValue = value.coerceIn(minValue, maxValue)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        val barY = height / 2
        val barLength = width * 0.8f
        val barStartX = (width - barLength) / 2
        val barEndX = barStartX + barLength

        // Draw background (white line)
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = barHeight
        canvas.drawLine(barStartX, barY, barEndX, barY, paint)

        // Draw filled progress (red based on ratio)
        val progressX = barStartX + (barLength * (currentValue - minValue) / (maxValue - minValue))
        paint.color = Color.RED
        paint.style = Paint.Style.FILL
        rect.set(barStartX, barY - barHeight / 2, progressX, barY + barHeight / 2)
        canvas.drawRect(rect, paint)

        // Draw labels above the bar
        val textY = barY - barHeight - 10f
        canvas.drawText("${(minValue / 1000).roundToInt()}", barStartX, textY, textPaint)
        canvas.drawText("${(maxValue / 1000).roundToInt()}", barEndX, textY, textPaint)
        val midValue = minValue + (maxValue - minValue) / 2
        canvas.drawText("${(midValue / 1000).roundToInt()}", (barStartX + barEndX) / 2, textY, textPaint)
        canvas.drawText("rpm x1000", (barStartX + barEndX) / 2, textY + 40f, textPaint)
    }
}