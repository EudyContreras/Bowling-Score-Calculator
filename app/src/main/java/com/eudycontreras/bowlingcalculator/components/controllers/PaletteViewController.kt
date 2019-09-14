package com.eudycontreras.bowlingcalculator.components.controllers

import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.components.views.PaletteViewComponent
import com.eudycontreras.bowlingcalculator.libraries.morpher.Morpher
import com.eudycontreras.bowlingcalculator.utilities.extensions.addTouchAnimation
import com.eudycontreras.bowlingcalculator.utilities.properties.Palette

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class PaletteViewController(
    val context: MainActivity,
    scoreController: ScoreController
) {

    private var viewComponent: PaletteViewComponent = PaletteViewComponent(context, this)

    init {
        scoreController.paletteController = this
    }

    fun assignInteraction(view: View?) {
        view?.addTouchAnimation(
            clickTarget = null,
            scale = 0.95f,
            interpolatorPress =  DecelerateInterpolator(),
            interpolatorRelease = OvershootInterpolator()
        )
    }

    fun show(duration: Long) {
       viewComponent.show(duration)
    }

    fun dismiss(duration: Long) {
        viewComponent.dismiss(duration)
    }

    fun applyThemeWith(color: Int) {
        val palette = Palette.of(color)
        context.onNewPalette(palette)
        context.morphTransitioner.startingState.color = color
        dismiss(Morpher.DEFAULT_DURATION)
    }
}