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
import androidx.constraintlayout.widget.ConstraintLayout
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
class ConstraintLayout : ConstraintLayout,
    MorphLayout {

    override var morphX: Float
        get() = this@ConstraintLayout.x
        set(value) {
            this@ConstraintLayout.x = value
        }
    override var morphY: Float
        get() = this@ConstraintLayout.y
        set(value) {
            this@ConstraintLayout.y = value
        }
    override var morphWidth: Float
        get() = this@ConstraintLayout.width.toFloat()
        set(value) {
            this@ConstraintLayout.layoutParams.width = value.toInt()
        }
    override var morphHeight: Float
        get() = this@ConstraintLayout.height.toFloat()
        set(value) {
            this@ConstraintLayout.layoutParams.height = value.toInt()
        }
    override var morphAlpha: Float
        get() = this@ConstraintLayout.alpha
        set(value) {
            this@ConstraintLayout.alpha = value
        }
    override var morphElevation: Float
        get() = this@ConstraintLayout.elevation
        set(value) {
            this@ConstraintLayout.elevation = value
        }
    override var morphTranslationX: Float
        get() = this@ConstraintLayout.translationX
        set(value) {
            this@ConstraintLayout.translationX = value
        }
    override var morphTranslationY: Float
        get() = this@ConstraintLayout.translationY
        set(value) {
            this@ConstraintLayout.translationY = value
        }
    override var morphTranslationZ: Float
        get() = this@ConstraintLayout.translationZ
        set(value) {
            this@ConstraintLayout.translationZ = value
        }
    override var morphPivotX: Float
        get() = this@ConstraintLayout.pivotX
        set(value) {
            this@ConstraintLayout.pivotX = value
        }
    override var morphPivotY: Float
        get() = this@ConstraintLayout.pivotY
        set(value) {
            this@ConstraintLayout.pivotY = value
        }
    override var morphRotation: Float
        get() = this@ConstraintLayout.rotation
        set(value) {
            this@ConstraintLayout.rotation = value
        }
    override var morphRotationX: Float
        get() = this@ConstraintLayout.rotationX
        set(value) {
            this@ConstraintLayout.rotationX = value
        }
    override var morphRotationY: Float
        get() = this@ConstraintLayout.rotationY
        set(value) {
            this@ConstraintLayout.rotationY = value
        }
    override var morphScaleX: Float
        get() = this@ConstraintLayout.scaleX
        set(value) {
            this@ConstraintLayout.scaleX = value
        }
    override var morphScaleY: Float
        get() = this@ConstraintLayout.scaleY
        set(value) {
            this@ConstraintLayout.scaleY = value
        }
    override var morphColor: Int
        get() = this@ConstraintLayout.getColor()
        set(value) {
            this@ConstraintLayout.backgroundTintList = value.toStateList()
        }
    override var morphStateList: ColorStateList?
        get() = this@ConstraintLayout.backgroundTintList
        set(value) {
            this@ConstraintLayout.backgroundTintList = value
        }
    override var morphCornerRadii: CornerRadii
        get() = cornerRadii
        set(value) {
            cornerRadii = value
        }
    override val morphChildCount: Int
        get() = this@ConstraintLayout.childCount

    override var morphVisibility: Int
        get() = this@ConstraintLayout.visibility
        set(value) {
            this@ConstraintLayout.visibility = value
        }
    override var showMutateCorners: Boolean = true

    override val morphTag: Any?
        get() = this@ConstraintLayout.tag

    private var drawListener: DrawDispatchListener? = null

    private lateinit var mutableDrawable: GradientDrawable

    private var cornerRadii: CornerRadii = CornerRadii()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setUpAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setUpAttributes(attrs)
    }

    private fun setUpAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ConstraintLayout)
        try {
            val shape = typedArray.getInt(R.styleable.ConstraintLayout_cl_shapeType,
                RECTANGULAR
            )
            val radius = typedArray.getDimension(R.styleable.ConstraintLayout_cl_radius, 0f)
            val topLeft = typedArray.getDimension(R.styleable.ConstraintLayout_cl_topLeftCornerRadius, radius)
            val topRight = typedArray.getDimension(R.styleable.ConstraintLayout_cl_topRightCornerRadius, radius)
            val bottomRight = typedArray.getDimension(R.styleable.ConstraintLayout_cl_bottomRightCornerRadius, radius)
            val bottomLeft = typedArray.getDimension(R.styleable.ConstraintLayout_cl_bottomLeftCornerRadius, radius)

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
