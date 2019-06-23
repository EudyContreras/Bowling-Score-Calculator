package com.eudycontreras.bowlingcalculator.components.views

import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.components.controllers.ActionViewController
import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_PIN_COUNT
import com.eudycontreras.bowlingcalculator.utilities.extensions.addTouchAnimation
import com.eudycontreras.bowlingcalculator.utilities.extensions.clamp
import com.eudycontreras.bowlingcalculator.utilities.runSequential
import com.eudycontreras.bowlingcalculator.utilities.toString
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class ActionsViewComponent(
    private val context: MainActivity,
    private val controller: ActionViewController
) : ViewComponent {

    private var remainingPins: Int = DEFAULT_PIN_COUNT

    private val parentView: View? = context.actionArea

    private val interpolatorIn: Interpolator = DecelerateInterpolator()
    private val interpolatorOut: Interpolator = OvershootInterpolator()

    private val throwAction: LinearLayout? = context.findViewById(R.id.throwAction)

    private val strikeAction: View? = parentView?.findViewById(R.id.actionStrike)
    private val resetAction: View? = parentView?.findViewById(R.id.actionClearScore)
    private val loadSaveAction: View? = parentView?.findViewById(R.id.actionLoadSaveResult)

    private val loadAction: View? = loadSaveAction?.findViewById(R.id.actionLoadResult)
    private val saveAction: View? = loadSaveAction?.findViewById(R.id.actionSaveResult)

    init {
        setDefaultValues()
        registerListeners()

        assignInteraction(strikeAction)
        assignInteraction(resetAction)
        assignInteraction(loadAction)
        assignInteraction(saveAction)
    }

    override fun setDefaultValues() {
        throwAction?.let { parent ->
            parent.children.forEachIndexed { index, view ->
                val input: FrameLayout = view.findViewById(R.id.throwInput) as FrameLayout
                val text: TextView = input.findViewById(R.id.throwInputText)

                view.scaleY = 0f
                view.scaleX = 0f
                text.text = toString(index)

                input.addTouchAnimation(
                    clickTarget = null,
                    scale = 0.93f,
                    interpolatorPress = interpolatorIn,
                    interpolatorRelease = interpolatorOut
                )
            }
        }
    }

    override fun registerListeners() {
        throwAction?.let { parent ->
            parent.children.forEachIndexed { index, view ->
                view.findViewById<FrameLayout>(R.id.throwInput).setOnClickListener {
                    controller.handleThrowActions(index)
                }
            }
        }

        strikeAction?.setOnClickListener {
            controller.handleThrowActions(DEFAULT_PIN_COUNT)
        }

        resetAction?.setOnClickListener {
            controller.handleResetAction()
        }

        loadAction?.setOnClickListener {
            controller.handleLoadAction()
        }

        saveAction?.setOnClickListener {
            controller.handleSaveAction()
        }
    }

    override fun assignInteraction(view: View?) {
        view?.addTouchAnimation(
            clickTarget = null,
            scale = 0.95f,
            interpolatorPress = interpolatorIn,
            interpolatorRelease = interpolatorOut
        )
    }

    fun revealAvailablePins() {
        val delay: Long =  150
        val duration: Long = 250

        throwAction?.let { parent ->
            var leftIndex = (parent.childCount / 2) - 1
            var rightIndex = parent.childCount / 2
            runSequential(delay, parent.childCount - 1) {
                context.runOnUiThread {
                    val view = parent.getChildAt(leftIndex)
                    view.isEnabled = true
                    view.animate()
                        .alpha(1f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setInterpolator(OvershootInterpolator())
                        .setDuration(duration)
                        .start()
                    leftIndex = (leftIndex - 1).clamp(0, 4)
                }
                context.runOnUiThread {
                    val view = parent.getChildAt(rightIndex)
                    view.isEnabled = true
                    view.animate()
                        .alpha(1f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setInterpolator(OvershootInterpolator())
                        .setDuration(duration)
                        .start()
                    rightIndex = (rightIndex + 1).clamp(5, 9)
                }
            }
        }
    }

    fun setAvailablePins(remainingPins: Int) {
        this.remainingPins = remainingPins
        if (remainingPins < DEFAULT_PIN_COUNT) {
            deactivateStrike(strikeAction)
        } else {
            activateStrike(strikeAction)
        }
        throwAction?.let { parent ->
            parent.children.forEachIndexed { index, view ->
                val input: FrameLayout = view.findViewById(R.id.throwInput)
                if ((index) > remainingPins) {
                    hidePinInput(input)
                } else {
                    showPinsInput(input)
                }
            }
        }
    }

    fun revealArea() {

    }

    fun concealArea() {

    }

    private fun activateStrike(view: View?) {
        view?.let {
            it.isEnabled = true
            it.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(250)
                .setInterpolator(OvershootInterpolator())
                .start()
        }
    }

    private fun deactivateStrike(view: View?) {
        view?.let {
            it.isEnabled = false
            it.animate()
                .alpha(0.0f)
                .scaleX(0.4f)
                .scaleY(0.4f)
                .setDuration(250)
                .setInterpolator(AccelerateInterpolator())
                .start()

        }
    }

    private fun hidePinInput(view: View) {
        view.isEnabled = false
        view.animate()
            .alpha(0f)
            .setDuration(250)
            .start()
    }

    private fun showPinsInput(view: View) {
        view.isEnabled = true
        view.animate()
            .alpha(1f)
            .setDuration(250)
            .start()
    }
}