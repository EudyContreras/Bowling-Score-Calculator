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
import android.widget.LinearLayout
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
class LinearLayout : LinearLayout, MorphLayout {

    override var morphX: Float
        get() = this@LinearLayout.x
        set(value) {
            this@LinearLayout.x = value
        }
    override var morphY: Float
        get() = this@LinearLayout.y
        set(value) {
            this@LinearLayout.y = value
        }
    override var morphWidth: Float
        get() = this@LinearLayout.width.toFloat()
        set(value) {
            this@LinearLayout.layoutParams.width = value.toInt()
        }
    override var morphHeight: Float
        get() = this@LinearLayout.height.toFloat()
        set(value) {
            this@LinearLayout.layoutParams.height = value.toInt()
        }
    override var morphAlpha: Float
        get() = this@LinearLayout.alpha
        set(value) {
            this@LinearLayout.alpha = value
        }
    override var morphElevation: Float
        get() = this@LinearLayout.elevation
        set(value) {
            this@LinearLayout.elevation = value
        }
    override var morphTranslationX: Float
        get() = this@LinearLayout.translationX
        set(value) {
            this@LinearLayout.translationX = value
        }
    override var morphTranslationY: Float
        get() = this@LinearLayout.translationY
        set(value) {
            this@LinearLayout.translationY = value
        }
    override var morphTranslationZ: Float
        get() = this@LinearLayout.translationZ
        set(value) {
            this@LinearLayout.translationZ = value
        }
    override var morphPivotX: Float
        get() = this@LinearLayout.pivotX
        set(value) {
            this@LinearLayout.pivotX = value
        }
    override var morphPivotY: Float
        get() = this@LinearLayout.pivotY
        set(value) {
            this@LinearLayout.pivotY = value
        }
    override var morphRotation: Float
        get() = this@LinearLayout.rotation
        set(value) {
            this@LinearLayout.rotation = value
        }
    override var morphRotationX: Float
        get() = this@LinearLayout.rotationX
        set(value) {
            this@LinearLayout.rotationX = value
        }
    override var morphRotationY: Float
        get() = this@LinearLayout.rotationY
        set(value) {
            this@LinearLayout.rotationY = value
        }
    override var morphScaleX: Float
        get() = this@LinearLayout.scaleX
        set(value) {
            this@LinearLayout.scaleX = value
        }
    override var morphScaleY: Float
        get() = this@LinearLayout.scaleY
        set(value) {
            this@LinearLayout.scaleY = value
        }
    override var morphColor: Int
        get() = this@LinearLayout.getColor()
        set(value) {
            this@LinearLayout.backgroundTintList = value.toStateList()
        }
    override var morphStateList: ColorStateList?
        get() = this@LinearLayout.backgroundTintList
        set(value) {
            this@LinearLayout.backgroundTintList = value
        }
    override var morphCornerRadii: CornerRadii
        get() = cornerRadii
        set(value) {
            updateCorners(value)
        }
    override var showMutateCorners: Boolean = true

    override val morphChildCount: Int
        get() = this@LinearLayout.childCount

    override var morphVisibility: Int
        get() = this@LinearLayout.visibility
        set(value) {
            this@LinearLayout.visibility = value
        }
    override val morphTag: Any?
        get() = this@LinearLayout.tag

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
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LinearLayout)
        try {
            val shape = typedArray.getInt(R.styleable.LinearLayout_ll_shapeType,
                RECTANGULAR
            )
            val radius = typedArray.getDimension(R.styleable.LinearLayout_ll_radius, 0f)
            val topLeft = typedArray.getDimension(R.styleable.LinearLayout_ll_topLeftCornerRadius, radius)
            val topRight = typedArray.getDimension(R.styleable.LinearLayout_ll_topRightCornerRadius, radius)
            val bottomRight = typedArray.getDimension(R.styleable.LinearLayout_ll_bottomRightCornerRadius, radius)
            val bottomLeft = typedArray.getDimension(R.styleable.LinearLayout_ll_bottomLeftCornerRadius, radius)

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
