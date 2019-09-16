package com.eudycontreras.bowlingcalculator.activities

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.components.controllers.*
import com.eudycontreras.bowlingcalculator.libraries.morpher.Morpher
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.morphLayouts.FrameLayout
import com.eudycontreras.bowlingcalculator.listeners.AnimationListener
import com.eudycontreras.bowlingcalculator.listeners.BackPressedListener
import com.eudycontreras.bowlingcalculator.listeners.PaletteListener
import com.eudycontreras.bowlingcalculator.utilities.BowlerListener
import com.eudycontreras.bowlingcalculator.utilities.Bowlers
import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_GRACE_PERIOD
import com.eudycontreras.bowlingcalculator.utilities.extensions.addTouchAnimation
import com.eudycontreras.bowlingcalculator.utilities.extensions.app
import com.eudycontreras.bowlingcalculator.utilities.properties.Palette
import com.eudycontreras.bowlingcalculator.utilities.runAfterMain
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_toolbar.view.*



/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

class MainActivity : PaletteListener, AppCompatActivity() {

    private val backNavigationListeners = ArrayList<BackPressedListener>()

    private lateinit var scoreController: ScoreController

    private lateinit var emptyStateController: EmptyStateViewController
    private lateinit var inputNameController: InputViewController
    private lateinit var paletteController: PaletteViewController
    private lateinit var loaderController: LoaderViewController
    private lateinit var framesController: FramesViewController
    private lateinit var actionController: ActionViewController
    private lateinit var statsController: StatsViewController
    private lateinit var tabsController: TabsViewController

    lateinit var morphTransitioner: Morpher

    private var created = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(com.eudycontreras.bowlingcalculator.R.layout.activity_main)
        setSupportActionBar(toolbar as Toolbar)

        registerListeners()
        initControllers()
        setDefaults()
    }

    private fun setDefaults() {
        morphTransitioner = Morpher(this)

        morphTransitioner.morphIntoInterpolator = FastOutSlowInInterpolator()
        morphTransitioner.morphFromInterpolator = FastOutSlowInInterpolator()

        morphTransitioner.useArcTranslator = true

        onNewPalette(Palette.of(app.persistenceManager.getActiveThemeColor()))
    }

    private fun initControllers() {
        scoreController = ScoreController(this)

        emptyStateController = EmptyStateViewController(this, this.emptyStateArea, scoreController)
        inputNameController = InputViewController(this, scoreController)
        paletteController = PaletteViewController(this, scoreController)
        loaderController = LoaderViewController(this, scoreController)
        framesController = FramesViewController(this, scoreController)
        actionController = ActionViewController(this, scoreController)
        statsController = StatsViewController(this, scoreController)
        tabsController = TabsViewController(this, scoreController)

        tabsController.createTabs(emptyList())

        if (app.persistenceManager.hasBowlers()) {
            loaderController.showLoader()
        }
    }

    private fun registerListeners() {
        toolbar.toolbarMenu.addTouchAnimation(
            clickTarget = null,
            scale = 0.90f,
            depth = 0f,
            interpolatorPress = DecelerateInterpolator(),
            interpolatorRelease = OvershootInterpolator()
        )

        toolbar.toolbarMenu.setOnClickListener {
            morphTransitioner.startView = toolbar.toolbarMenu
            paletteController.show(Morpher.DEFAULT_DURATION)
        }
    }

    override fun onBackPressed() {
        if (morphTransitioner.isMorphing) {
            return
        }
        if (morphTransitioner.isMorphed) {
            hideOverlay()
            morphTransitioner.morphFrom()
            return
        }
        backNavigationListeners.forEach {
            it.onBackPressed()
        }

        if (!backNavigationListeners.any { it.disallowExit() }) {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!created){
            created = true
            if (app.persistenceManager.hasBowlers()) {
                runAfterMain(DEFAULT_GRACE_PERIOD / 2) {
                    scoreController.onStorageFull(app.persistenceManager)
                }
            } else {
                runAfterMain(DEFAULT_GRACE_PERIOD) {
                    scoreController.onStorageEmpty()
                }
            }
        }
    }

    fun addBackPressListeners(listener: BackPressedListener) {
        backNavigationListeners.add(listener)
    }

    fun removeBackPressListeners(listener: BackPressedListener) {
        backNavigationListeners.remove(listener)
    }

    override fun onDestroy() {
        val bowlers = scoreController.bowlers
        app.persistenceManager.updateBowlers(bowlers)
        super.onDestroy()
    }

    fun saveCurrentState(bowlers: Bowlers, listener: BowlerListener = null) {
        app.persistenceManager.saveBowlers(bowlers, listener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == com.eudycontreras.bowlingcalculator.R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun openDialog(fragment: DialogFragment, showTransition: Boolean = false) {
        val prev = supportFragmentManager.findFragmentByTag(fragment::class.java.simpleName)

        val fragmentTransaction = supportFragmentManager.beginTransaction()

        if (supportFragmentManager.fragments.contains(fragment) || prev != null) {
            fragmentTransaction.remove(prev!!)
        }

        if (!showTransition) {
            fragmentTransaction.setCustomAnimations(0,0)
        }
        fragment.show(fragmentTransaction, fragment::class.java.simpleName)
    }

    fun showOverlay(duration: Long = Morpher.DEFAULT_DURATION) {
        dimOverlay.animate()
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(FastOutSlowInInterpolator())
            .setListener(AnimationListener(
                onStart = {
                    dimOverlay.visibility = View.VISIBLE
                },
                onEnd = {
                    dimOverlay.setOnClickListener {
                       hideOverlay()
                       morphTransitioner.morphFrom()
                }
            }))
            .start()
    }

    fun hideOverlay(duration: Long = Morpher.DEFAULT_DURATION) {
        dimOverlay.setOnClickListener(null)
        dimOverlay.animate()
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(FastOutSlowInInterpolator())
            .setListener(AnimationListener(onEnd = {
                dimOverlay.visibility = View.GONE
            }))
            .start()
    }

    override fun onNewPalette(palette: Palette) {
        emptyStateController.onNewPalette(palette)
        loaderController.onNewPalette(palette)
        tabsController.onNewPalette(palette)

        throwAction.backgroundTintList = ColorStateList.valueOf(palette.colorPrimary)
        actionArea.backgroundTintList = ColorStateList.valueOf(palette.colorPrimary)
        toolbar.backgroundTintList = ColorStateList.valueOf(palette.colorPrimary)

        (toolbar.toolbarMenu as FrameLayout).setBackgroundColor(palette.colorPrimary)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = palette.colorPrimaryDark
        }
    }
}
