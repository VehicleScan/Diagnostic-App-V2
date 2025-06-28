package com.example.diagnostic333

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.*

class SeekBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 18f
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
    }
    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 36f
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        color = Color.rgb(255, 191, 0)
    }

    private var minValue = 0f
    private var maxValue = 10000f
    private var currentValue = 0f
    private val dashCount = 21
    private val neonRed = Color.parseColor("#FF1744")
    private val inactiveColor = Color.parseColor("#444444")
    private val activeColor = Color.WHITE
    private val arcRadius = 200f
    private val spacing = 40f

    fun configure(min: Float, max: Float, startValue: Float = min) {
        require(min < max) { "min must be < max" }
        minValue = min
        maxValue = max
        currentValue = startValue.coerceIn(min, max)
        invalidate()
    }

    fun updateValue(value: Float) {
        currentValue = value.coerceIn(minValue, maxValue)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = (arcRadius + 20f + 30f + 36f + 20f).toInt() // arc + bar + label + value + padding
        val height = resolveSize(desiredHeight, heightMeasureSpec)
        val width = resolveSize(suggestedMinimumWidth, widthMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 3f
        val centerY = arcRadius + 150f
        val ratio = (currentValue - minValue) / (maxValue - minValue)
        val activeDashes = (dashCount * ratio).toInt()
        val thresholdIndex = (dashCount * 0.75f).toInt()

        val segmentPositions = mutableListOf<Pair<Float, Float>>()

        // First half (curved)
        val arcSegmentCount = dashCount / 2
        val arcStartAngle = 180f
        val arcSweep = 90f
        val arcSegmentAngle = arcSweep / (arcSegmentCount - 1)

        for (i in 0 until arcSegmentCount) {
            val angle = Math.toRadians((arcStartAngle + i * arcSegmentAngle).toDouble())
            val x = centerX + arcRadius * cos(angle).toFloat()
            val y = centerY + arcRadius * sin(angle).toFloat()
            segmentPositions.add(Pair(x, y))
        }

        // Second half (straight)
        val (startX, startY) = segmentPositions.last()
        val straightSegmentCount = dashCount - arcSegmentCount
        for (i in 1..straightSegmentCount) {
            val x = startX + i * spacing
            val y = startY
            segmentPositions.add(Pair(x, y))
        }

        // Draw segments
        val valuePerSegment = (maxValue - minValue) / (dashCount - 1)
        for (i in 0 until dashCount) {
            val (x, y) = segmentPositions[i]
            barPaint.color = when {
                i >= thresholdIndex && i < activeDashes -> neonRed
                i < activeDashes -> activeColor
                else -> inactiveColor
            }

            canvas.drawRoundRect(
                RectF(x - 10f, y - 20f, x + 10f, y + 20f),
                6f, 6f, barPaint
            )

            val precent = i.toFloat()/(dashCount-1)
            if(precent in listOf(0f , 0.25f,0.5f,0.75f,1f)){
                if(precent == 0f){
                    val labelValue= (minValue+precent*(maxValue - minValue)).roundToInt()
                    canvas.drawText("$labelValue",x-30f,y-20f,labelPaint)
                }else{
                    val labelValue= (minValue+precent*(maxValue - minValue)).roundToInt()
                    canvas.drawText("$labelValue",x,y-30f,labelPaint)
                }

            }
        }

        // Draw current value below center segment
        val centerIndex = dashCount / 2
        val (vx, vy) = segmentPositions[centerIndex]
        canvas.drawText("${currentValue.roundToInt()} km/h", vx, vy + 100f, valuePaint)
    }
}
