package com.ext.quick_contacts_picker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat

/**
 * Custom view for fast alphabet scrolling with highlight + big letter overlay popup
 */
class AlphabetScrollerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ#".toList()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var letterHeight = 0f
    private var onLetterSelectedListener: ((String) -> Unit)? = null
    private var highlightedLetter: String? = null

    // Overlay reference (automatically found in parent layout)
    private var letterOverlay: TextView? = null

    private var textColor: Int = 0
    private var selectedTextColor: Int = 0
    private var textSize: Float = 0f
    private var highlightBackground: Int = 0

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.AlphabetScrollerView, 0, 0).apply {
            try {
                textColor = getColor(
                    R.styleable.AlphabetScrollerView_scrollerTextColor,
                    ContextCompat.getColor(context, android.R.color.darker_gray)
                )
                selectedTextColor = getColor(
                    R.styleable.AlphabetScrollerView_scrollerSelectedTextColor,
                    ContextCompat.getColor(context, R.color.primary_color)
                )
                textSize = getDimension(
                    R.styleable.AlphabetScrollerView_scrollerTextSize,
                    12f * resources.displayMetrics.scaledDensity
                )
                highlightBackground = getColor(
                    R.styleable.AlphabetScrollerView_scrollerHighlightBackground,
                    ContextCompat.getColor(context, R.color.primary_light_color)
                )
            } finally {
                recycle()
            }
        }

        paint.color = textColor
        paint.textSize = textSize
        paint.textAlign = Paint.Align.CENTER

        highlightPaint.color = highlightBackground
        highlightPaint.style = Paint.Style.FILL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidthPx = (20 * resources.displayMetrics.density).toInt()
        val desiredWidth = (paint.textSize * 2.5f).toInt().coerceAtLeast(minWidthPx)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val finalWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> desiredWidth.coerceAtMost(widthSize)
            else -> desiredWidth
        }.coerceAtLeast(minWidthPx)

        setMeasuredDimension(finalWidth, MeasureSpec.getSize(heightMeasureSpec))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (height == 0) return
        letterHeight = height.toFloat() / letters.size
        val xPos = width / 2f

        letters.forEachIndexed { index, letter ->
            val yPos = (index * letterHeight) + letterHeight / 2 + (paint.textSize / 3)
            val letterStr = letter.toString()

            if (letterStr == highlightedLetter) {
                val centerY = (index * letterHeight) + letterHeight / 2
                val radius = letterHeight / 2.2f
                canvas.drawCircle(xPos, centerY, radius, highlightPaint)

                paint.color = selectedTextColor
                canvas.drawText(letterStr, xPos, yPos, paint)
                paint.color = textColor
            } else {
                canvas.drawText(letterStr, xPos, yPos, paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val y = event.y.coerceIn(0f, height.toFloat())
        val index = (y / letterHeight).toInt().coerceIn(0, letters.size - 1)
        val letter = letters[index].toString()

        when (event.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                setHighlightedLetter(letter)
                onLetterSelectedListener?.invoke(letter)
                showBigLetterOverlay(letter)  // New overlay logic
                return true
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                hideBigLetterOverlay()        // Hide on release
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun showBigLetterOverlay(letter: String) {
        ensureOverlay()
        letterOverlay?.let { overlay ->
            overlay.text = letter
            if (overlay.visibility != View.VISIBLE) {
                overlay.visibility = View.VISIBLE
                overlay.scaleX = 0f
                overlay.scaleY = 0f
                overlay.alpha = 0f
                overlay.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(150)
                    .start()
            }
        }
    }

    private fun hideBigLetterOverlay() {
        letterOverlay?.animate()
            ?.scaleX(0f)
            ?.scaleY(0f)
            ?.alpha(0f)
            ?.setDuration(150)
            ?.withEndAction { letterOverlay?.visibility = View.GONE }
            ?.start()
    }

    private fun ensureOverlay() {
        if (letterOverlay != null) return

        var parent = parent
        while (parent is ViewGroup) {
            val found = parent.findViewById<TextView>(R.id.letterOverlay)
            if (found != null) {
                letterOverlay = found
                return
            }
            parent = parent.parent
        }
    }

    fun setOnLetterSelectedListener(listener: (String) -> Unit) {
        onLetterSelectedListener = listener
    }

    fun setHighlightedLetter(letter: String) {
        if (highlightedLetter != letter) {
            highlightedLetter = letter
            invalidate()
        }
    }
}