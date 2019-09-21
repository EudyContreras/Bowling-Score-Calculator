package com.eudycontreras.bowlingcalculator.components.views

import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.components.controllers.PaletteViewController
import com.eudycontreras.bowlingcalculator.libraries.morpher.Morpher
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.morphLayouts.ConstraintLayout
import com.eudycontreras.bowlingcalculator.listeners.PaletteListener
import com.eudycontreras.bowlingcalculator.utilities.extensions.addTouchAnimation
import com.eudycontreras.bowlingcalculator.utilities.properties.Palette
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_app_settings.view.*

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

class ThemeSelectViewComponent(
    private val context: MainActivity,
    val controller: PaletteViewController
) : PaletteListener, ViewComponent() {

    private val parentView: ConstraintLayout = context.settings as ConstraintLayout

    private lateinit var themes: Array<View>

    init {
        setDefaultValues()
        registerListeners()
    }

    override fun setDefaultValues() {
        assignInteraction(parentView.dialogSettingsCancel)
        themes = arrayOf(
            parentView.theme0,
            parentView.theme1,
            parentView.theme2,
            parentView.theme3,
            parentView.theme4,
            parentView.theme5
        )
        themes.forEach {
            assignInteraction(it)
        }
    }
    override fun registerListeners() {
        parentView.dialogSettingsCancel.setOnClickListener {
            controller.dismiss(Morpher.DEFAULT_DURATION)
        }
        themes.forEach { view ->
            view.setOnClickListener {
                it.backgroundTintList?.let { color ->
                    controller.applyThemeWith(color.defaultColor)
                }
            }
        }
    }

    override fun assignInteraction(view: View?) {
        view?.addTouchAnimation(
            clickTarget = null,
            scale = 0.95f,
            interpolatorPress =  DecelerateInterpolator(),
            interpolatorRelease = OvershootInterpolator()
        )
    }

    fun show(duration: Long) {
        context.showOverlay(duration)
        context.morphTransitioner.endView = parentView
        context.morphTransitioner.morphInto(duration, onEnd = {

        })
    }

    fun dismiss(duration: Long) {
        context.hideOverlay(duration)
        context.morphTransitioner.morphFrom(duration, onEnd = {

        })
    }

    override fun onNewPalette(palette: Palette) {

    }
}