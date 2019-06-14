package com.eudycontreras.bowlingcalculator.components

import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import com.eudycontreras.bowlingcalculator.DEFAULT_PIN_COUNT
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.SHOW_SAVE_BUTTON
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.components.controllers.ActionViewController
import com.eudycontreras.bowlingcalculator.extensions.addTouchAnimation
import com.eudycontreras.bowlingcalculator.toString
import com.eudycontreras.bowlingcalculatorchallenge.view_components.ViewComponent
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by eudycontreras.
 */

class ActionsViewComponent(
    context: MainActivity,
    private val controller: ActionViewController
) : ViewComponent {

    private val parentView: View? = context.actionArea

    private val interpolatorIn: Interpolator = DecelerateInterpolator()
    private val interpolatorOut: Interpolator = OvershootInterpolator()

    private val throwAction: LinearLayout? = context.findViewById(R.id.throwAction)

    private val strikeAction: View? = parentView?.findViewById(R.id.actionStrike)
    private val resetAction: View? = parentView?.findViewById(R.id.actionClearScore)
    private val saveAction: View? = parentView?.findViewById(R.id.actionSaveScore)

    init {
        setDefaultValues()
        registerListeners()

        assignInteraction(strikeAction)
        assignInteraction(resetAction)
        assignInteraction(saveAction)
    }

    override fun setDefaultValues() {
        saveAction?.visibility = if (SHOW_SAVE_BUTTON) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }

        throwAction?.let { parent ->
            parent.children.forEachIndexed { index, view ->
                val input: FrameLayout = view.findViewById(R.id.throwInput) as FrameLayout
                val text: TextView = input.findViewById(R.id.throwInputText)
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

    fun setAvailablePins(remainingPins: Int) {
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