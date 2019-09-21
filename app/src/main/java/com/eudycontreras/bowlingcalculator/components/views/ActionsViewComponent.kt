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
import com.eudycontreras.bowlingcalculator.listeners.PaletteListener
import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_PIN_COUNT
import com.eudycontreras.bowlingcalculator.utilities.extensions.addTouchAnimation
import com.eudycontreras.bowlingcalculator.utilities.extensions.clamp
import com.eudycontreras.bowlingcalculator.utilities.properties.Palette
import com.eudycontreras.bowlingcalculator.utilities.runSequential
import com.eudycontreras.bowlingcalculator.utilities.toString
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

class ActionsViewComponent(
    context: MainActivity,
    private val controller: ActionViewController
) : PaletteListener, ViewComponent() {

    private var remainingPins: Int = DEFAULT_PIN_COUNT

    private val parentView: View? = context.actionArea

    private val interpolatorIn: Interpolator = DecelerateInterpolator()
    private val interpolatorOut: Interpolator = OvershootInterpolator()

    private val throwAction: LinearLayout? = context.findViewById(R.id.throwAction)

    private val strikeAction: View? = parentView?.findViewById(R.id.actionStrike)
    private val strikeActionIcon: View? = parentView?.findViewById(R.id.actionStrikeIcon)
    private val resetAction: View? = parentView?.findViewById(R.id.actionClearScore)
    private val loadSaveAction: View? = parentView?.findViewById(R.id.actionLoadSaveResult)
    private val spareAction: View? = parentView?.findViewById(R.id.actionSpare)

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

                input.scaleY = 0f
                input.scaleX = 0f
                input.alpha = 0f
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
                    controller.performThrow(index)
                }
            }
        }

        strikeAction?.setOnClickListener {
            controller.performThrow(DEFAULT_PIN_COUNT)
        }

        resetAction?.setOnClickListener {
            controller.performReset()
        }

        loadAction?.setOnClickListener {
            controller.loadResult()
        }

        saveAction?.setOnClickListener {
            controller.saveResult()
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
        val duration: Long = 350
        val delay: Long = 60

        throwAction?.let { parent ->
            var leftIndex = (parent.childCount / 2) - 1
            var rightIndex = parent.childCount / 2
            val onEnd = {
                activateStrike(strikeAction)
            }
            runSequential(delay, parent.childCount - 1, onEnd) {
                val viewLeft = parent.getChildAt(leftIndex).findViewById<FrameLayout>(R.id.throwInput)
                viewLeft.isEnabled = true
                viewLeft.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setInterpolator(OvershootInterpolator())
                    .setDuration(duration)
                    .start()
                leftIndex = (leftIndex - 1).clamp(0, 4)
                val viewRight = parent.getChildAt(rightIndex).findViewById<FrameLayout>(R.id.throwInput)
                viewRight.isEnabled = true
                viewRight.animate()
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

    fun setAvailablePins(remainingPins: Int, duration: Long) {
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
                    hidePinInput(input, duration)
                } else {
                    showPinsInput(input, duration)
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
                .setDuration(300)
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
                .setDuration(200)
                .setInterpolator(AccelerateInterpolator())
                .start()

        }
    }

    private fun hidePinInput(view: View, duration: Long = 300) {
        view.isEnabled = false
        view.animate()
            .alpha(0f)
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(duration)
            .start()
    }

    private fun showPinsInput(view: View, duration: Long = 300) {
        view.isEnabled = true
        view.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(duration)
            .start()
    }

    override fun onNewPalette(palette: Palette) {
        //resetAction?.backgroundTintList = palette.colorPrimary.toStateList()
        //strikeActionIcon?.backgroundTintList = palette.colorPrimary.toStateList()
        //spareAction?.backgroundTintList = palette.colorPrimary.toStateList()
    }
}