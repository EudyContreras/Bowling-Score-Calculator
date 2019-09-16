package com.eudycontreras.bowlingcalculator.components.views

import android.view.View
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.components.controllers.LoaderViewController
import com.eudycontreras.bowlingcalculator.listeners.PaletteListener
import com.eudycontreras.bowlingcalculator.utilities.extensions.color
import com.eudycontreras.bowlingcalculator.utilities.extensions.dp
import com.eudycontreras.bowlingcalculator.utilities.properties.Palette
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

class LoaderViewComponent(
    private val context: MainActivity,
    val controller: LoaderViewController
) : ViewComponent(), PaletteListener {

    private val loader = context.progressLoader

    private var hideDuration: Long = 300
    private var showDuration: Long = 300

    init {
        setDefaultValues()
    }

    override fun setDefaultValues() {
        loader.scaleX = 0.5f
        loader.scaleY = 0.5f
        loader.alpha = 0f
        loader.translationZ = 0f
        loader.indeterminateDrawable.setColorFilter(context.color(R.color.theme0), android.graphics.PorterDuff.Mode.SRC_ATOP)
    }

    override fun registerListeners() { }

    override fun assignInteraction(view: View?) { }

    fun hideLoader() {
        loader.animate()
            .translationZ(0f)
            .scaleX(0.5f)
            .scaleY(0.5f)
            .alpha(0f)
            .setInterpolator(AnticipateInterpolator())
            .setDuration(hideDuration)
            .start()
    }

    fun showLoader() {
        loader.animate()
            .translationZ(8.dp)
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setInterpolator(OvershootInterpolator())
            .setDuration(showDuration)
            .start()
    }

    override fun onNewPalette(palette: Palette) {
        val color = palette.colorPrimaryLight
        loader.indeterminateDrawable.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP)
    }
}