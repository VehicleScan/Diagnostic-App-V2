package com.example.diagnostic333

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.roundToInt

class DigitalNumberWindowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = Color.RED // Blue border
    }

    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 90f
        typeface = Typeface.createFromAsset(context.assets, "fonts/ds_digital.TTF") // Custom font
        textAlign = Paint.Align.CENTER
        color = Color.RED // Red digits
        setShadowLayer(10f, 0f, 0f, Color.RED) // Glow effect
    }

    private var minValue = 0f
    private var maxValue = 9999f
    private var currentValue = 0f
    private val cornerRadius = 25f

    init {
        configure(minValue, maxValue, valuePaint.color)
    }

    fun configure(min: Float, max: Float, color: Int) {
        require(min < max) { "minValue must be less than maxValue" }
        minValue = min
        maxValue = max
        valuePaint.color = color
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
        val padding = 30f

        val rect = RectF(padding, padding, width - padding, height - padding)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, backgroundPaint)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, borderPaint)

        val valueText = currentValue.roundToInt().toString().padStart(4, '0')
        val valueY = height / 2 - (valuePaint.descent() + valuePaint.ascent()) / 2
        canvas.drawText(valueText, width / 2, valueY, valuePaint)
    }
}
