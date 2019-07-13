package com.eudycontreras.bowlingcalculator.libraries.morpher.efffectShapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.Bounds

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
class ParticleReveal : Particle {

    private var x: Float = 0f
    private var y: Float = 0f

    private var width: Float = 0f
    private var height: Float = 0f

    private var maxWidth: Float = 0f
    private var maxHeight: Float = 0f

    private var minWidth: Float = 0f
    private var minHeight: Float = 0f

    private var maxRadii: FloatArray = FloatArray(4)
    private var minRadii: FloatArray = FloatArray(4)
    private val radii: FloatArray = FloatArray(8)

    private lateinit var clipPath: Path

    constructor() : super(0f, 0f, 0f, 0, Paint(), Bounds())

    constructor(x: Float, y: Float, radius: Float, color: Int, bounds: Bounds) : super(x, y, radius, color, bounds)

    constructor(x: Float, y: Float, radius: Float, color: Int, paint: Paint, bounds: Bounds) : super(
        x,
        y,
        radius,
        color,
        paint,
        bounds
    )

    override fun init() {
        clipPath = Path()
        clipPath.reset()
    }

    override fun update(duration: Long, time: Float) {
        width = minWidth + (maxWidth - minWidth) * time
        height = minHeight + (maxHeight - minHeight) * time

        radii[0] = minRadii[0] + (maxRadii[0] - minRadii[0]) * time
        radii[1] = minRadii[0] + (maxRadii[0] - minRadii[0]) * time

        radii[2] = minRadii[1] + (maxRadii[1] - minRadii[1]) * time
        radii[3] = minRadii[1] + (maxRadii[1] - minRadii[1]) * time

        radii[4] = minRadii[2] + (maxRadii[2] - minRadii[2]) * time
        radii[5] = minRadii[2] + (maxRadii[2] - minRadii[2]) * time

        radii[6] = minRadii[3] + (maxRadii[3] - minRadii[3]) * time
        radii[7] = minRadii[3] + (maxRadii[3] - minRadii[3]) * time

        x += (targetX - x) * time
        y += (targetY - y) * time
    }

    override fun isAlive(): Boolean {
        return width < maxWidth
    }

    public override fun draw(canvas: Canvas) {
        val top = y
        val left = x
        val bottom = y + height
        val right = x + width

        clipPath.rewind()
        clipPath.addRoundRect(left, top, right, bottom, radii, Path.Direction.CCW)

        canvas.clipPath(clipPath)
    }

    fun setX(x: Float) {
        this.x = x
    }

    fun setY(y: Float) {
        this.y = y
    }

    fun setMaxWidth(maxWidth: Float) {
        this.maxWidth = maxWidth
    }

    fun setMaxHeight(maxHeight: Float) {
        this.maxHeight = maxHeight
    }

    fun setMinWidth(minWidth: Float) {
        this.minWidth = minWidth
    }

    fun setMinHeight(minHeight: Float) {
        this.minHeight = minHeight
    }

    fun setMaxRadii(maxRadii: FloatArray) {
        this.maxRadii = maxRadii
    }

    fun setMaxRadii(radii: Int) {
        this.maxRadii[0] = radii.toFloat()
        this.maxRadii[1] = radii.toFloat()
        this.maxRadii[2] = radii.toFloat()
        this.maxRadii[3] = radii.toFloat()
    }

    fun setMinRadii(minRadii: FloatArray) {
        this.minRadii = minRadii
    }

    fun setMinRadii(radii: Float) {
        this.minRadii[0] = radii
        this.minRadii[1] = radii
        this.minRadii[2] = radii
        this.minRadii[3] = radii
    }
}