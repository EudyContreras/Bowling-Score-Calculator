package com.eudycontreras.bowlingcalculator.libraries.morpher.effects

import android.graphics.Canvas
import android.graphics.Paint
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.Bounds
import com.eudycontreras.bowlingcalculator.libraries.morpher.utilities.ColorUtility
import com.eudycontreras.indicatoreffectlib.particles.Particle

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
abstract class Particle {

    protected var centerX: Float = 0f
    protected var centerY: Float = 0f

    protected var velX: Float = 0f
    protected var velY: Float = 0f

    protected var varianceX: Float = 0f
    protected var varianceY: Float = 0f

    protected var radiusRatio = 0.0f
    protected var actualRadius: Float = 0f
    protected var radius: Float = 0f
    protected var spacing: Float = 0f
    protected var opacity = 1.0f

    protected var lifeSpan = 1.0f
    protected var decay: Float = 0f

    protected var checkBounds = false
    protected var visible: Boolean = false
    protected var killed: Boolean = false
    protected var shrink = true
    protected var fade = true

    var targetX = Float.MIN_VALUE
    var targetY = Float.MIN_VALUE

    var color: ColorUtility.MorphColor? = null
    var bounds: Bounds = Bounds()
    var paint: Paint = Paint()

    protected constructor(lifeTime: Float, x: Float, y: Float, velX: Float, velY: Float, varianceX: Float, varianceY: Float, radius: Float, color: Int, paint: Paint, bounds: Bounds){
        this.centerX = x
        this.centerY = y
        this.velX = velX
        this.velY = velY
        this.varianceX = varianceX
        this.varianceY = varianceY
        this.radius = radius
        this.color = ColorUtility.toSoulColor(color)
        this.bounds = bounds
        this.decay = 0.016f / lifeTime
        this.paint = paint
    }

    protected constructor(lifeTime: Float, x: Float, y: Float, velX: Float, velY: Float, radius: Float, color: Int, paint: Paint, bounds: Bounds):
            this(lifeTime, x, y, velX, velY, 0f, 0f, radius, color, paint, bounds)

    protected constructor(lifeTime: Float, x: Float, y: Float, radius: Float, color: Int, paint: Paint, bounds: Bounds):
            this(lifeTime, x, y, 0f, 0f, radius, color, paint, bounds)

    protected constructor(x: Float, y: Float, radius: Float, color: Int, paint: Paint, bounds: Bounds):
            this(Integer.MAX_VALUE.toFloat(), x, y, 0f, 0f, radius, color, paint, bounds)

    protected constructor(x: Float, y: Float, radius: Float, color: Int, bounds: Bounds):
            this(Float.MAX_VALUE, x, y, 0f, 0f, radius, color, Paint(), bounds)

    protected constructor(): this(0f, 0f, 0f, 0x000000, Bounds())

    abstract fun init()

    abstract fun draw(canvas: Canvas)

    open fun update() {
        centerX += velX + varianceX
        centerY += velY + varianceY

        if (killed) {
            lifeSpan -= decay
        }
    }

    open fun update(duration: Long, time: Float) {

        velX = if (targetX != Float.MIN_VALUE) (targetX - centerX) / duration else velX
        velY = if (targetY != Float.MIN_VALUE) (targetY - centerY) / duration else velY

        centerX += (velX + varianceX) * time
        centerY += (velY + varianceY) * time

        if (shrink) {
            radiusRatio = time
            radius = actualRadius * radiusRatio
        }

        if (killed) {
            lifeSpan -= decay * time
        } else {
            if (fade) {
                opacity = time
            }
        }
    }

    open fun checkDistanceTo(particle: Particle): Float {

        val distanceX = this.centerX + radius - particle.centerX + radius
        val distanceY = this.centerY + radius - particle.centerY + radius

        return distanceX * distanceX + distanceY * distanceY
    }

    open fun isAlive(): Boolean {
        return if (checkBounds) {
            (bounds.inRange(centerX, centerY, radius * 2)) && lifeSpan > 0 && radius > 0 && opacity > 0
        } else lifeSpan > 0
    }

    companion object {
        const val DEFAULT_LIFE_TIME = 5f
        const val DEFAULT_VELOCITY = 5f
    }
}
