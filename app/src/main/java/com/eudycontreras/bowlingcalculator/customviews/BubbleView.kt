package com.eudycontreras.bowlingcalculator.customviews

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.utilities.properties.Bounds
import com.eudycontreras.bowlingcalculator.utilities.properties.Coordinate
import com.eudycontreras.bowlingcalculator.utilities.properties.Dimension
import com.eudycontreras.bowlingcalculator.utilities.properties.MutableColor
import com.eudycontreras.bowlingcalculator.utilities.extensions.dp


/**
 * Created by eudycontreras.
 */
class BubbleView : ViewGroup {

    companion object {
        const val DEFAULT_ELEVATION = 6f
        const val DEFAULT_CORNER_RADIUS = 20f
        const val DEFAULT_POINTER_LENGTH = 30f
        const val DEFAULT_POINTER_WIDTH = 20f
    }

    private var initialized: Boolean = false

    private var bubble: Bubble = Bubble()

    private val path: Path = Path()

    private var color: Int = 0xfffff

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BubbleView)
        try {
            setUpAttributes(typedArray)
        } finally {
            typedArray.recycle()
        }
    }
    init {
        val background = background
        color = if (background is ColorDrawable)  background.color else color

        setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
        //setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    private fun setUpAttributes(typedArray: TypedArray) {
        bubble.elevation = typedArray.getDimension(R.styleable.BubbleView_bubble_elevation, DEFAULT_ELEVATION.dp)
        bubble.pointerWidth = typedArray.getDimension(R.styleable.BubbleView_bubble_pointer_width, DEFAULT_POINTER_WIDTH.dp)
        bubble.pointerLength = typedArray.getDimension(R.styleable.BubbleView_bubble_pointer_length, DEFAULT_POINTER_LENGTH.dp)
        bubble.cornerRadius = typedArray.getDimension(R.styleable.BubbleView_bubble_corner_radius, DEFAULT_CORNER_RADIUS.dp)
    }

    private fun setPosition(x: Float, y: Float) {
        bubble.coordinate.x = x
        bubble.coordinate.y = y
    }

    private fun initializeValues(left: Float, top: Float, right: Float, bottom: Float): Bounds {

        val width = measuredWidth
        val height = measuredHeight

        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        val paddingTop = paddingTop
        val paddingBottom = paddingBottom

        val usableWidth = width - (paddingLeft + paddingRight).toFloat()
        val usableHeight = height - (paddingTop + paddingBottom).toFloat()

        val location = IntArray(2)
        getLocationInWindow(location)

        val bounds = Bounds(Coordinate(left, top), Dimension(usableWidth, usableHeight))

        bubble.render = true
        bubble.drawShadow = true
        bubble.shadow?.let {
            it.shadowColor = MutableColor.rgb(0)
            it.minStepCount = 0f
            it.maxStepCount = 12f
        }

        bubble.build(bounds)
        bubble.color = MutableColor.fromColor(color)

        return bubble.bounds.drawableArea
    }

    override fun dispatchDraw(canvas: Canvas) {
        bubble.render(path, paint, canvas)
        super.dispatchDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        initialized = false
       // initializeValues()

      /*  for (child in children) {
            child.measure(widthSpec, heightSpec)
        }*/
        invalidate()
    }


    override fun onLayout(onChange: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        initialized = false

        val bounds = initializeValues(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
//        val offset = (bubble.elevation * 2).toInt()
//
//        for (child in children) {
//            child.layout(
//                bubble.elevation.toInt(),
//                bubble.elevation.toInt(),
//                right - bubble.elevation.toInt(),
//                bounds.bottom.toInt() - (bubble.pointerLength + offset + bubble.cornerRadius).toInt())
//
//            child.invalidate()
//        }

        invalidate()
    }

    private val myListener =  object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent): Boolean {
            return true
        }

        override fun onLongPress(event: MotionEvent) {
            super.onLongPress(event)
            parent.requestDisallowInterceptTouchEvent(true)
            bubble.onLongPressed(event, event.x, event.y)

            invalidate()
        }
    }

    private val detector: GestureDetector = GestureDetector(context, myListener)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return detector.onTouchEvent(event).let { result ->

            invalidate()

            val x = event.x
            val y = event.y

            bubble.onTouch(event, x, y)

            when (event.action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }

            result
        }
    }
}