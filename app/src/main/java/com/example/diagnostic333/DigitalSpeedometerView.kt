package com.example.diagnostic333

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.roundToInt

class DigitalSpeedometerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.DKGRAY
    }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.WHITE
    }
    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 80f
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }
    private val unitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 30f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
    }

    private var minValue = 0f
    private var maxValue = 10000f
    private var currentValue = 0f
    private var displayColor = Color.WHITE
    private val cornerRadius = 20f
    private var unitLabel = "rpm x1000"

    init {
        configure(0f, 10000f, Color.WHITE)
    }

    fun configure(min: Float, max: Float, color: Int, unit: String = "rpm x1000") {
        require(min < max) { "minValue must be less than maxValue" }
        minValue = min
        maxValue = max
        displayColor = color
        unitLabel = unit
        currentValue = minValue
        valuePaint.color = displayColor
        invalidate()
    }

    fun updateValue(value: Float) {
        currentValue = value.coerceIn(minValue, maxValue)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        val padding = 20f
        val contentWidth = width - 2 * padding
        val contentHeight = height - 2 * padding

        // Draw background with border
        val rect = RectF(padding, padding, width - padding, height - padding)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, backgroundPaint)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, borderPaint)

        // Draw speed value
        val valueText = currentValue.roundToInt().toString()
        val valueY = height / 2 - (valuePaint.descent() + valuePaint.ascent()) / 2
        canvas.drawText(valueText, width / 2, valueY, valuePaint)

        // Draw unit label
        val unitY = valueY + valuePaint.textSize + 10f
        canvas.drawText(unitLabel, width / 2, unitY, unitPaint)
    }
}