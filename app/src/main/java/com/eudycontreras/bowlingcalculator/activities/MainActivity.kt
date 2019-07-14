package com.eudycontreras.bowlingcalculator.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.components.controllers.*
import com.eudycontreras.bowlingcalculator.libraries.morpher.Morpher
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.MorphLayout
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.morphLayouts.FrameLayout
import com.eudycontreras.bowlingcalculator.listeners.BackPressedListener
import com.eudycontreras.bowlingcalculator.utilities.BowlerListener
import com.eudycontreras.bowlingcalculator.utilities.Bowlers
import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_GRACE_PERIOD
import com.eudycontreras.bowlingcalculator.utilities.extensions.addTouchAnimation
import com.eudycontreras.bowlingcalculator.utilities.extensions.app
import com.eudycontreras.bowlingcalculator.utilities.runAfterMain
import com.eudycontreras.indicatoreffectlib.views.IndicatorView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_toolbar.view.*

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class MainActivity : AppCompatActivity() {

    private val backNavigationListeners = ArrayList<BackPressedListener>()

    private lateinit var scoreController: ScoreController

    private lateinit var emptyStateController: EmptyStateViewController
    private lateinit var inputNameController: InputViewController
    private lateinit var loaderController: LoaderViewController
    private lateinit var framesController: FramesViewController
    private lateinit var actionController: ActionViewController
    private lateinit var statsController: StatsViewController
    private lateinit var tabsController: TabsViewController

    lateinit var indicator: IndicatorView

    val morphTransitioner = Morpher()

    private var created = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar as Toolbar)

        initIndicator()
        registerListeners()
        initControllers()
        setDefaults()
    }

    private fun setDefaults() {
        morphTransitioner.startView = toolbar.toolbarMenuBorder
        morphTransitioner.endView = dialog as MorphLayout

        morphTransitioner.morphIntoInterpolator = FastOutSlowInInterpolator()
        morphTransitioner.morphFromInterpolator = AccelerateDecelerateInterpolator()

        morphTransitioner.useArcTranslator = true

        transitionSeekBar.max = 100
    }

    private fun initControllers() {
        scoreController = ScoreController(this)

        emptyStateController = EmptyStateViewController(this, this.emptyStateArea, scoreController)
        inputNameController = InputViewController(this, scoreController)
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

    private fun initIndicator() {
        indicator = IndicatorView(this, findViewById<ViewGroup>(R.id.root))
        indicator.indicatorType = IndicatorView.INDICATOR_TYPE_AROUND
        indicator.indicatorColor = ContextCompat.getColor(this, R.color.colorAccentLight)
        indicator.indicatorStrokeColor = ContextCompat.getColor(this, R.color.colorAccentLight)
        indicator.indicatorColorStart = ContextCompat.getColor(this, R.color.white)
        indicator.indicatorColorEnd = ContextCompat.getColor(this, R.color.colorAccentLight)
        indicator.indicatorCount = 3
        indicator.indicatorMinOpacity = 0f
        indicator.indicatorMaxOpacity = 1f
        indicator.indicatorRepeatMode = IndicatorView.REPEAT_MODE_RESTART
        indicator.indicatorRepeats = IndicatorView.INFINITE_REPEATS
        indicator.indicatorDuration = 4000
        indicator.indicatorStrokeWidth = 0f
        indicator.isShowBorderStroke = false
        indicator.revealDuration = 500
        indicator.revealDuration = 0
        indicator.isUseColorInterpolation = false
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
            morphTransitioner.morphInto(1350)
        }

        dialog.findViewById<FrameLayout>(R.id.createDialogAddInput).addTouchAnimation(
            clickTarget = null,
            scale = 0.90f,
            depth = 0f,
            interpolatorPress = DecelerateInterpolator(),
            interpolatorRelease = OvershootInterpolator()
        )

        dialog.findViewById<FrameLayout>(R.id.createDialogAddInput).setOnClickListener {
            morphTransitioner.morphFrom(1350)
        }

        transitionSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(view: SeekBar?, progress: Int, fromUser: Boolean) {
                morphTransitioner.transitionBy(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
    }

    override fun onBackPressed() {
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
                    onStorageFull()
                }
            } else {
                runAfterMain(DEFAULT_GRACE_PERIOD) {
                    onStorageEmpty()
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

    private fun onStorageEmpty() {
        tabsController.onTabRequested(false)
    }

    private fun onStorageFull() {
        val bowlers = app.persistenceManager.getBowlers()
        val activeTab = app.persistenceManager.getActiveTab()

        bowlers.observe(this, Observer {
            if (it.isEmpty()) {
                onStorageEmpty()
                return@Observer
            }

            loaderController.hideLoader()
            scoreController.initCalculator(it, activeTab)
            tabsController.addTabs(it, activeTab, false)
        })
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

        if (id == R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun openDialog(fragment: DialogFragment) {
        val prev = supportFragmentManager.findFragmentByTag(fragment::class.java.simpleName)

        val fragmentTransaction = supportFragmentManager.beginTransaction()

        if (supportFragmentManager.fragments.contains(fragment) || prev != null) {
            fragmentTransaction.remove(prev!!)
        }

        fragmentTransaction.addToBackStack(null)
        fragment.show(fragmentTransaction, fragment::class.java.simpleName)
    }
}
