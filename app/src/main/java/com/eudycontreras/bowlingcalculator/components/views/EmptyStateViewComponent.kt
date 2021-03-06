package com.eudycontreras.bowlingcalculator.components.views

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.components.controllers.EmptyStateViewController
import com.eudycontreras.bowlingcalculator.listeners.AnimationListener
import com.eudycontreras.bowlingcalculator.listeners.PaletteListener
import com.eudycontreras.bowlingcalculator.utilities.extensions.*
import com.eudycontreras.bowlingcalculator.utilities.properties.Palette

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

class EmptyStateViewComponent(
    context: MainActivity,
    val parent: View,
    val controller: EmptyStateViewController
) : ViewComponent(), PaletteListener{

    private val shape: View = parent.findViewById(R.id.emptyStateShape)
    private val icon: View = parent.findViewById(R.id.emptyStateIcon)
    private val action: View = parent.findViewById(R.id.emptyStateAction)
    private val title: TextView = parent.findViewById(R.id.emptyStateTitle)
    private val body: TextView = parent.findViewById(R.id.emptyStateBody)

    private val interpolatorIn: Interpolator = DecelerateInterpolator()
    private val interpolatorOut: Interpolator = OvershootInterpolator()

    private var emptyState: EmptyState = EmptyState.Main(context, null)

    private var showing = false

    init {
        setDefaultValues()
        registerListeners()
    }

    override fun setDefaultValues() {
        showing = false

        parent.visibility = View.INVISIBLE
        icon.background = emptyState.icon
        title.text = emptyState.title
        body.text = emptyState.body
        emptyState.iconColor?.let {
            icon.backgroundTintList = ColorStateList.valueOf(it)
        }

        action.scaleX = 0f
        action.scaleY = 0f
        action.translationZ = 0f

        title.alpha = 0f
        body.alpha = 0f
    }

    override fun registerListeners() {
        if (emptyState.showActionButton) {
            assignInteraction(action)
            action.setOnClickListener {
                action.alpha = 0f
                emptyState.action?.invoke(shape)
            }
        } else {
            action.visibility = View.GONE
        }
    }

    override fun assignInteraction(view: View?) {
        view?.addTouchAnimation(
            clickTarget = null,
            scale = 0.95f,
            depth = 5.dp,
            originalDepth = 12.dp,
            interpolatorPress = interpolatorIn,
            interpolatorRelease = interpolatorOut
        )
    }

    fun setEmptyState(state: EmptyState) {
        if(emptyState == state && showing){
            return
        }

        this.emptyState = state

        setDefaultValues()
        registerListeners()
    }

    fun revealState(onEnd: (() -> Unit)?) {
        if (showing)
            return

        parent.visibility = View.VISIBLE

        val duration = 400L
        val endAction = {
            onEnd?.invoke()
            showing = true
        }

        shape.animate()
            .alpha(1f)
            .scaleY(1f)
            .scaleX(1f)
            .translationZ(4.dp)
            .setDuration(duration)
            .setInterpolator(OvershootInterpolator())
            .setListener(AnimationListener(onEnd = endAction))
            .start()

        action.animate()
            .alpha(1f)
            .scaleY(1f)
            .scaleX(1f)
            .translationZ(12.dp)
            .setStartDelay(duration - 50)
            .setDuration(duration)
            .setInterpolator(OvershootInterpolator())
            .start()

        title.animate()
            .alpha(1f)
            .setDuration(duration)
            .start()

        body.animate()
            .alpha(1f)
            .setDuration(duration)
            .start()

    }

    fun concealState(onEnd: (() -> Unit)?) {
        if (!showing)
            return

        val duration = 250L

        val endAction = {
            onEnd?.invoke()
            showing = false
            parent.visibility = View.INVISIBLE
        }

        shape.animate()
            .scaleY(0.6f)
            .scaleX(0.6f)
            .alpha(0f)
            .translationZ(0f)
            .setDuration(duration)
            .setInterpolator(FastOutLinearInInterpolator())
            .setListener(AnimationListener(onEnd = endAction))
            .start()

        action.animate()
            .setListener(null)
            .alpha(0f)
            .translationZ(4.dp)
            .setStartDelay(0)
            .setDuration(duration)
            .setInterpolator(OvershootInterpolator())
            .start()

        title.animate()
            .alpha(0f)
            .setDuration(duration)
            .start()

        body.animate()
            .alpha(0f)
            .setDuration(duration)
            .start()
    }

    sealed class EmptyState {
        abstract val icon: Drawable
        abstract val iconColor: Int?
        abstract val shapeColor: Int
        abstract val actionColor: Int
        abstract val titleIcon: Drawable
        abstract val actionIcon: Drawable
        abstract var title: String
        abstract var body: String
        abstract val action: ((View) -> Unit)?
        abstract val showActionButton: Boolean

        class Main(context: Activity, override val action: ((View) -> Unit)? = null) : EmptyState() {
            override val icon: Drawable = context.drawable(R.drawable.img_logo)
            override val iconColor: Int? = null
            override val actionColor: Int = context.color(R.color.colorAccentLight)
            override val shapeColor: Int = context.color(R.color.colorPrimary)
            override val titleIcon: Drawable = context.drawable(R.drawable.ic_add_bowler)
            override val actionIcon: Drawable = context.drawable(R.drawable.ic_add)
            override var title: String = context.getString(R.string.empty_state_add_bowler_title)
            override var body: String = context.getString(R.string.empty_state_add_bowler_body)
            override val showActionButton: Boolean = true
        }

        class Results(context: Activity, override val action: ((View) -> Unit)? = null) : EmptyState() {
            override val icon: Drawable = context.drawable(R.drawable.ic_list)
            override val iconColor: Int? = context.color(R.color.white)
            override val actionColor: Int = context.color(R.color.colorAccentLight)
            override val shapeColor: Int = context.color(R.color.colorAccent)
            override val titleIcon: Drawable = context.drawable(R.drawable.ic_add_bowler)
            override val actionIcon: Drawable = context.drawable(R.drawable.ic_add)
            override var title: String = context.getString(R.string.empty_state_results_title)
            override var body: String = context.getString(R.string.empty_state_results_body)
            override val showActionButton: Boolean = false
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is EmptyState) return false

            if (title != other.title) return false
            if (body != other.body) return false

            return true
        }

        override fun hashCode(): Int {
            var result = title.hashCode()
            result = 31 * result + body.hashCode()
            return result
        }
    }

    override fun onNewPalette(palette: Palette) {
        action.backgroundTintList = palette.colorPrimaryLight.toStateList()
    }
}
