package com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.morphLayouts

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.TextView
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.libraries.morpher.drawables.MorphTransitionDrawable
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.MorphLayout
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.MorphShape
import com.eudycontreras.bowlingcalculator.libraries.morpher.extensions.getColor
import com.eudycontreras.bowlingcalculator.libraries.morpher.listeners.DrawDispatchListener
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.CornerRadii
import com.eudycontreras.bowlingcalculator.utilities.extensions.toStateList

/**
 * Copyright (C) 2019 Motion Morpher Project
 *
 **Note:** Unlicensed private property of the author and creator
 * unauthorized use of this class outside of the Motion Morpher project
 * or other projects to which the author has explicitly added this library
 * may result on legal prosecution.
 *
 * @Project Motion Morpher
 * @author Eudy Contreras.
 * @since March 2019
 */
class TextLayout : TextView, MorphLayout {

    override var view: View = this

    override var morphX: Float
        get() = this.x
        set(value) {
            this.x = value
        }
    override var morphY: Float
        get() = this.y
        set(value) {
            this.y = value
        }
    override var morphWidth: Float
        get() = this.width.toFloat()
        set(value) {
            this.layoutParams.width = value.toInt()
        }
    override var morphHeight: Float
        get() = this.height.toFloat()
        set(value) {
            this.layoutParams.height = value.toInt()
        }
    override var morphAlpha: Float
        get() = this.alpha
        set(value) {
            this.alpha = value
        }
    override var morphElevation: Float
        get() = this.elevation
        set(value) {
            this.elevation = value
        }
    override var morphTranslationX: Float
        get() = this.translationX
        set(value) {
            this.translationX = value
        }
    override var morphTranslationY: Float
        get() = this.translationY
        set(value) {
            this.translationY = value
        }
    override var morphTranslationZ: Float
        get() = this.translationZ
        set(value) {
            this.translationZ = value
        }
    override var morphPivotX: Float
        get() = this.pivotX
        set(value) {
            this.pivotX = value
        }
    override var morphPivotY: Float
        get() = this.pivotY
        set(value) {
            this.pivotY = value
        }
    override var morphRotation: Float
        get() = this.rotation
        set(value) {
            this.rotation = value
        }
    override var morphRotationX: Float
        get() = this.rotationX
        set(value) {
            this.rotationX = value
        }
    override var morphRotationY: Float
        get() = this.rotationY
        set(value) {
            this.rotationY = value
        }
    override var morphScaleX: Float
        get() = this.scaleX
        set(value) {
            this.scaleX = value
        }
    override var morphScaleY: Float
        get() = this.scaleY
        set(value) {
            this.scaleY = value
        }
    override var morphColor: Int
        get() = this.getColor()
        set(value) {
            this.backgroundTintList = value.toStateList()
        }
    override var morphStateList: ColorStateList?
        get() = this.backgroundTintList
        set(value) {
            this.backgroundTintList = value
        }
    override var morphCornerRadii: CornerRadii
        get() = cornerRadii
        set(value) {
            updateCorners(value)
        }
    override val morphChildCount: Int
        get() = 0

    override var morphVisibility: Int
        get() = this.visibility
        set(value) {
            this.visibility = value
        }
    override var mutateCorners: Boolean = true

    override val morphTag: Any?
        get() = this.tag

    override val windowLocationX: Int
        get() {
            this.getLocationInWindow(location)
            return location[0]
        }
    override val windowLocationY: Int
        get() {
            this.getLocationInWindow(location)
            return location[1]
        }

    override var morphBackground: Drawable
        get() = background
        set(value) {
            this.background = value
        }

    override val morphShape: Int
        get() = shape

    private var shape: Int = RECTANGULAR

    private val location: IntArray = IntArray(2)

    private var cornerRadii: CornerRadii = CornerRadii()

    private var drawListener: DrawDispatchListener? = null

    private lateinit var mutableDrawable: GradientDrawable

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
            shape = typedArray.getInt(R.styleable.LinearLayout_ll_shapeType,
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

        if (background is VectorDrawable || background is BitmapDrawable) {
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
            mutateCorners = false
        }

        mutableDrawable = drawable
        background = drawable
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (shape == CIRCULAR && background !is VectorDrawable) {

            val drawable = (background as GradientDrawable).mutate() as GradientDrawable

            val corners = floatArrayOf(
                w.toFloat(), h.toFloat(),
                w.toFloat(), h.toFloat(),
                w.toFloat(), h.toFloat(),
                w.toFloat(), h.toFloat()
            )

            drawable.cornerRadii = corners

            cornerRadii = CornerRadii(corners)

            mutableDrawable = drawable
            background = drawable
        }
    }

    override fun hasVectorDrawable(): Boolean {
        return background is VectorDrawable
    }

    override fun hasBitmapDrawable(): Boolean {
        return background is BitmapDrawable
    }

    override fun hasGradientDrawable(): Boolean {
        return background is GradientDrawable
    }

    override fun hasMorphTransitionDrawable(): Boolean {
        return background is MorphTransitionDrawable
    }

    override fun getVectorDrawable(): VectorDrawable {
        return (background as VectorDrawable).mutate() as VectorDrawable
    }

    override fun getGradientBackground(): GradientDrawable {
        return mutableDrawable
    }

    override fun getBitmapDrawable(): BitmapDrawable {
        return background as BitmapDrawable
    }

    override fun getMorphTransitionDrawable(): MorphTransitionDrawable {
        return background as MorphTransitionDrawable
    }

    override fun applyTransitionDrawable(transitionDrawable: MorphTransitionDrawable) {
        this.background = transitionDrawable
    }

    override fun updateCorners(cornerRadii: CornerRadii): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return false
        }

        for (index in 0 until cornerRadii.size) {
            val corner = cornerRadii[index]
            cornerRadii[index] = corner
        }

        mutableDrawable.cornerRadii = cornerRadii.corners
        return true
    }

    override fun updateCorners(index: Int, corner: Float): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return false
        }

        cornerRadii[index] = corner
        mutableDrawable.cornerRadii = cornerRadii.corners
        return true
    }

    override fun getMorphShape(): MorphShape {
        return MorphShape()
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

    override fun setLayer(layer: Int) {
        setLayerType(layer, null)
    }

    override fun toString(): String = tag.toString()

    fun setListener(listener: DrawDispatchListener) {
        this.drawListener = listener
    }

    companion object {
        const val CIRCULAR = 0
        const val RECTANGULAR = 1
    }
}
