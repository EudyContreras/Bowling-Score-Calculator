package com.eudycontreras.bowlingcalculator.customviews

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import com.eudycontreras.bowlingcalculator.utilities.extensions.dp


class RoundedCornerLayout : FrameLayout {

    var cornerRadius: Float = DEFAULT_CORNER_RADIUS

    private var maskBitmap: Bitmap? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
    }

    private val maskPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val typedArray = context.obtainStyledAttributes(attrs, com.eudycontreras.bowlingcalculator.R.styleable.RoundedCornerLayout)
        try {
            setUpAttributes(typedArray)
        } finally {
            typedArray.recycle()
        }
    }

    private fun setUpAttributes(typedArray: TypedArray) {
        if (typedArray.hasValue(com.eudycontreras.bowlingcalculator.R.styleable.RoundedCornerLayout_rcl_corner_radius)) {
            cornerRadius = typedArray.getDimension(com.eudycontreras.bowlingcalculator.R.styleable.RoundedCornerLayout_rcl_corner_radius, DEFAULT_CORNER_RADIUS.dp)
        }
    }

    override fun draw(canvas: Canvas) {
        val offscreenBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val offscreenCanvas = Canvas(offscreenBitmap)

        super.draw(offscreenCanvas)

        if (maskBitmap == null) {
            maskBitmap = createMask(width, height)
        }

        offscreenCanvas.drawBitmap(maskBitmap!!, 0f, 0f, maskPaint)
        canvas.drawBitmap(offscreenBitmap, 0f, 0f, paint)
    }

    private fun createMask(width: Int, height: Int): Bitmap {
        val mask = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(mask)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val background = background
        paint.color = if (background is ColorDrawable) background.color else Color.WHITE

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), cornerRadius, cornerRadius, paint)

        return mask
    }

    companion object {
        const val DEFAULT_CORNER_RADIUS = 20f
    }
}