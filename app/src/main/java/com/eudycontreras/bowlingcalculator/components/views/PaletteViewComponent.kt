package com.eudycontreras.bowlingcalculator.components.views

import android.view.View
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.components.controllers.PaletteViewController
import com.eudycontreras.bowlingcalculator.libraries.morpher.Morpher
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.morphLayouts.ConstraintLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_app_settings.view.*

class PaletteViewComponent(
    private val context: MainActivity,
    val controller: PaletteViewController
) : ViewComponent {

    private val parentView: ConstraintLayout = context.settings as ConstraintLayout

    private lateinit var themes: Array<View>

    init {
        setDefaultValues()
        registerListeners()
    }

    override fun setDefaultValues() {
        assignInteraction(parentView.dialogSettingsCancel)
        themes = arrayOf(parentView.theme0, parentView.theme1, parentView.theme2, parentView.theme3, parentView.theme4)
        themes.forEach {
            assignInteraction(it)
        }
    }

    override fun registerListeners() {
        parentView.dialogSettingsCancel.setOnClickListener {
            controller.dismiss(Morpher.DEFAULT_DURATION)
        }
        themes.forEach {
            it.setOnClickListener {
                it.backgroundTintList?.let {
                    controller.applyThemeWith(it.defaultColor)
                }
            }
        }
    }

    override fun assignInteraction(view: View?) {
        controller.assignInteraction(view)
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
}