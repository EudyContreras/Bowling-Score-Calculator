package com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.morphLayouts

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.FrameLayout
import androidx.core.view.children
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.MorphLayout
import com.eudycontreras.bowlingcalculator.libraries.morpher.extensions.getColor
import com.eudycontreras.bowlingcalculator.libraries.morpher.listeners.DrawDispatchListener
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.CornerRadii
import com.eudycontreras.bowlingcalculator.utilities.extensions.toStateList

/**
 * <h1>Class description!</h1>
 *
 *
 *
 * **Note:** Unlicensed private property of the author and creator
 * unauthorized use of this class outside of the Soul Vibe project
 * may result on legal prosecution.
 *
 *
 * Created by <B>Eudy Contreras</B>
 *
 * @author  Eudy Contreras
 * @version 1.0
 * @since   2018-03-31
 */
class FrameLayout : FrameLayout, MorphLayout {

    override var morphX: Float
        get() = this@FrameLayout.x
        set(value) {
            this@FrameLayout.x = value
        }
    override var morphY: Float
        get() = this@FrameLayout.y
        set(value) {
            this@FrameLayout.y = value
        }
    override var morphWidth: Float
        get() = this@FrameLayout.width.toFloat()
        set(value) {
            this@FrameLayout.layoutParams.width = value.toInt()
        }
    override var morphHeight: Float
        get() = this@FrameLayout.layoutParams.height.toFloat()
        set(value) {
            this@FrameLayout.layoutParams.height = value.toInt()
        }
    override var morphAlpha: Float
        get() = this@FrameLayout.alpha
        set(value) {
            this@FrameLayout.alpha = value
        }
    override var morphElevation: Float
        get() = this@FrameLayout.elevation
        set(value) {
            this@FrameLayout.elevation = value
        }
    override var morphTranslationX: Float
        get() = this@FrameLayout.translationX
        set(value) {
            this@FrameLayout.translationX = value
        }
    override var morphTranslationY: Float
        get() = this@FrameLayout.translationY
        set(value) {
            this@FrameLayout.translationY = value
        }
    override var morphTranslationZ: Float
        get() = this@FrameLayout.translationZ
        set(value) {
            this@FrameLayout.translationZ = value
        }
    override var morphPivotX: Float
        get() = this@FrameLayout.pivotX
        set(value) {
            this@FrameLayout.pivotX = value
        }
    override var morphPivotY: Float
        get() = this@FrameLayout.pivotY
        set(value) {
            this@FrameLayout.pivotY = value
        }
    override var morphRotation: Float
        get() = this@FrameLayout.rotation
        set(value) {
            this@FrameLayout.rotation = value
        }
    override var morphRotationX: Float
        get() = this@FrameLayout.rotationX
        set(value) {
            this@FrameLayout.rotationX = value
        }
    override var morphRotationY: Float
        get() = this@FrameLayout.rotationY
        set(value) {
            this@FrameLayout.rotationY = value
        }
    override var morphScaleX: Float
        get() = this@FrameLayout.scaleX
        set(value) {
            this@FrameLayout.scaleX = value
        }
    override var morphScaleY: Float
        get() = this@FrameLayout.scaleY
        set(value) {
            this@FrameLayout.scaleY = value
        }
    override var morphColor: Int
        get() = this@FrameLayout.getColor()
        set(value) {
            this@FrameLayout.backgroundTintList = value.toStateList()
        }
    override var morphStateList: ColorStateList?
        get() = this@FrameLayout.backgroundTintList
        set(value) {
            this@FrameLayout.backgroundTintList = value
        }
    override var morphCornerRadii: CornerRadii
        get() = cornerRadii
        set(value) {
            updateCorners(value)
        }
    override val morphChildCount: Int
        get() = this@FrameLayout.childCount

    override var morphVisibility: Int
        get() = this@FrameLayout.visibility
        set(value) {
            this@FrameLayout.visibility = value
        }
    override var showMutateCorners: Boolean = true

    override val morphTag: Any?
        get() = this@FrameLayout.tag

    private lateinit var mutableDrawable: GradientDrawable

    private var cornerRadii: CornerRadii = CornerRadii()

    private var drawListener: DrawDispatchListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setUpAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setUpAttributes(attrs)
    }

    private fun setUpAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FrameLayout)
        try {
            val shape = typedArray.getInt(R.styleable.FrameLayout_fl_shapeType,
                RECTANGULAR
            )
            val radius = typedArray.getDimension(R.styleable.FrameLayout_fl_radius, 0f)
            val topLeft = typedArray.getDimension(R.styleable.FrameLayout_fl_topLeftCornerRadius, radius)
            val topRight = typedArray.getDimension(R.styleable.FrameLayout_fl_topRightCornerRadius, radius)
            val bottomRight = typedArray.getDimension(R.styleable.FrameLayout_fl_bottomRightCornerRadius, radius)
            val bottomLeft = typedArray.getDimension(R.styleable.FrameLayout_fl_bottomLeftCornerRadius, radius)

            applyDrawable(shape, topLeft, topRight, bottomRight, bottomLeft)
        } finally {
            typedArray.recycle()
        }
    }

    override fun applyDrawable(shape: Int, topLeft: Float, topRight: Float, bottomRight: Float, bottomLeft: Float) {
        var drawable = GradientDrawable()

        if (background is VectorDrawable) {
            return
        }

        drawable = if (background is GradientDrawable) {
            (background as GradientDrawable).mutate() as GradientDrawable
        } else {
            drawable.mutate() as GradientDrawable
        }

        if (backgroundTintList != null) {
            drawable.color = backgroundTintList
        } else {
            drawable.color = solidColor.toStateList()
        }

        drawable.shape = if (shape == RECTANGULAR) {
            GradientDrawable.RECTANGLE
        } else
            GradientDrawable.OVAL

        if (shape == RECTANGULAR) {
            val corners = floatArrayOf(
                topLeft, topLeft,
                topRight, topRight,
                bottomRight, bottomRight,
                bottomLeft, bottomLeft
            )

            drawable.cornerRadii = corners

            cornerRadii = CornerRadii(corners)
        } else {
            showMutateCorners = false
        }

        mutableDrawable = drawable
        background = drawable
    }

    override fun hasVectorDrawable(): Boolean {
        return background is VectorDrawable
    }

    override fun getVectorDrawable(): VectorDrawable {
        return (background as VectorDrawable).mutate() as VectorDrawable
    }

    override fun getGradientBackground(): GradientDrawable {
        return mutableDrawable
    }

    override fun updateCorners(cornerRadii: CornerRadii): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return false
        }

        for (index in 0 until cornerRadii.size) {
            val corner = cornerRadii[index]
            cornerRadii[index] = corner
        }

        mutableDrawable.cornerRadii = cornerRadii.asArray()
        return true
    }

    override fun updateCorners(index: Int, corner: Float): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return false
        }

        cornerRadii[index] = corner
        mutableDrawable.cornerRadii = cornerRadii.asArray()
        return true
    }

    override fun updateLayout() {
        requestLayout()
    }

    override fun animator(): ViewPropertyAnimator = animate()

    override fun getChildViewAt(index: Int): View = getChildAt(index)

    override fun hasChildren(): Boolean = childCount > 0

    override fun getChildren(): Sequence<View> {
        return children
    }

    override fun dispatchDraw(canvas: Canvas) {
        val save = canvas.save()

        drawListener?.onDrawDispatched(canvas)

        super.dispatchDraw(canvas)
        canvas.restoreToCount(save)
    }

    public override fun onDraw(canvas: Canvas) {
        val save = canvas.save()

        drawListener?.onDraw(canvas)

        super.dispatchDraw(canvas)
        canvas.restoreToCount(save)
    }

    fun setListener(listener: DrawDispatchListener) {
        this.drawListener = listener
    }

    companion object {
        const val RECTANGULAR = 1
    }
}
