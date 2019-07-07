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
class ViewLayout : View,
    MorphLayout {

    override var morphX: Float
        get() = this@ViewLayout.x
        set(value) {
            this@ViewLayout.x = value
        }
    override var morphY: Float
        get() = this@ViewLayout.y
        set(value) {
            this@ViewLayout.y = value
        }
    override var morphWidth: Float
        get() = this@ViewLayout.width.toFloat()
        set(value) {
            this@ViewLayout.layoutParams.width = value.toInt()
        }
    override var morphHeight: Float
        get() = this@ViewLayout.height.toFloat()
        set(value) {
            this@ViewLayout.layoutParams.height = value.toInt()
        }
    override var morphAlpha: Float
        get() = this@ViewLayout.alpha
        set(value) {
            this@ViewLayout.alpha = value
        }
    override var morphElevation: Float
        get() = this@ViewLayout.elevation
        set(value) {
            this@ViewLayout.elevation = value
        }
    override var morphTranslationX: Float
        get() = this@ViewLayout.translationX
        set(value) {
            this@ViewLayout.translationX = value
        }
    override var morphTranslationY: Float
        get() = this@ViewLayout.translationY
        set(value) {
            this@ViewLayout.translationY = value
        }
    override var morphTranslationZ: Float
        get() = this@ViewLayout.translationZ
        set(value) {
            this@ViewLayout.translationZ = value
        }
    override var morphPivotX: Float
        get() = this@ViewLayout.pivotX
        set(value) {
            this@ViewLayout.pivotX = value
        }
    override var morphPivotY: Float
        get() = this@ViewLayout.pivotY
        set(value) {
            this@ViewLayout.pivotY = value
        }
    override var morphRotation: Float
        get() = this@ViewLayout.rotation
        set(value) {
            this@ViewLayout.rotation = value
        }
    override var morphRotationX: Float
        get() = this@ViewLayout.rotationX
        set(value) {
            this@ViewLayout.rotationX = value
        }
    override var morphRotationY: Float
        get() = this@ViewLayout.rotationY
        set(value) {
            this@ViewLayout.rotationY = value
        }
    override var morphScaleX: Float
        get() = this@ViewLayout.scaleX
        set(value) {
            this@ViewLayout.scaleX = value
        }
    override var morphScaleY: Float
        get() = this@ViewLayout.scaleY
        set(value) {
            this@ViewLayout.scaleY = value
        }
    override var morphColor: Int
        get() = this@ViewLayout.getColor()
        set(value) {
            this@ViewLayout.backgroundTintList = value.toStateList()
        }
    override var morphStateList: ColorStateList?
        get() = this@ViewLayout.backgroundTintList
        set(value) {
            this@ViewLayout.backgroundTintList = value
        }
    override var morphCornerRadii: CornerRadii
        get() = cornerRadii
        set(value) {
            updateCorners(value)
        }
    override var showMutateCorners: Boolean = true

    override val morphChildCount: Int
        get() = 0

    override var morphVisibility: Int
        get() = this@ViewLayout.visibility
        set(value) {
            this@ViewLayout.visibility = value
        }
    override val morphTag: Any?
        get() = this@ViewLayout.tag

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
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewLayout)
        try {
            val shape = typedArray.getInt(R.styleable.ViewLayout_vl_shapeType,
                RECTANGULAR
            )
            val radius = typedArray.getDimension(R.styleable.ViewLayout_vl_radius, 0f)
            val topLeft = typedArray.getDimension(R.styleable.ViewLayout_vl_topLeftCornerRadius, radius)
            val topRight = typedArray.getDimension(R.styleable.ViewLayout_vl_topRightCornerRadius, radius)
            val bottomRight = typedArray.getDimension(R.styleable.ViewLayout_vl_bottomRightCornerRadius, radius)
            val bottomLeft = typedArray.getDimension(R.styleable.ViewLayout_vl_bottomLeftCornerRadius, radius)

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

    override fun hasChildren(): Boolean = false

    override fun getChildViewAt(index: Int): View = this

    override fun getChildren(): Sequence<View> {
        return emptySequence()
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
