package com.eudycontreras.bowlingcalculator.activities

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.eudycontreras.bowlingcalculator.DEFAULT_GRACE_PERIOD
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.components.controllers.ActionViewController
import com.eudycontreras.bowlingcalculator.components.controllers.FramesViewController
import com.eudycontreras.bowlingcalculator.components.controllers.StatsViewController
import com.eudycontreras.bowlingcalculator.components.controllers.TabsViewController
import com.eudycontreras.bowlingcalculator.extensions.app
import com.eudycontreras.bowlingcalculator.extensions.show
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var scoreController: ScoreController

    private lateinit var framesController: FramesViewController
    private lateinit var actionController: ActionViewController
    private lateinit var statsController: StatsViewController
    private lateinit var tabsController: TabsViewController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.eudycontreras.bowlingcalculator.R.layout.activity_main)

        setSupportActionBar(toolbar)

        initControllers()
    }

    private fun initControllers() {

        scoreController = ScoreController(this)

        framesController = FramesViewController(this, scoreController)
        actionController = ActionViewController(this, scoreController)
        statsController = StatsViewController(this, scoreController)
        tabsController = TabsViewController(this, scoreController)

        tabsController.createTabs(emptyList())
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({
            if (app.storage.currentBowlerIds.isNotEmpty()) {
                val bowlers = app.getBowlers(app.storage.currentBowlerIds)
                bowlers.observe(this, Observer {
                    val activeTab = app.storage.activeTab
                    scoreController.initCalculator(it, activeTab)
                    tabsController.addTabs(it)
                })

            } else {
                tabsController.requestTab{
                    val activeTab = tabsController.getActive()
                    app.storage.activeTab = activeTab
                    scoreController.initCalculator(it, activeTab)
                }
            }
        }, DEFAULT_GRACE_PERIOD)
    }


    override fun onDestroy() {
        super.onDestroy()
        saveCurrentState()
    }

    override fun onPause() {
        super.onPause()
        saveCurrentState()
    }

    private fun saveCurrentState() {
        app.storage.currentBowlerIds = scoreController.bowlers.map { it.id }.toLongArray()
        app.saveBowlers(scoreController.bowlers)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == com.eudycontreras.bowlingcalculator.R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun addFragment(fragment: Fragment, containerId: Int) {
        if (supportFragmentManager.fragments.contains(fragment)) {
            return
        }

        fragmentContainer.show()

        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.setCustomAnimations(0, 0, 0, 0)
        fragmentTransaction.add(containerId, fragment, Fragment::class.java.simpleName)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
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

    fun removeFragment(fragment: Fragment) {

        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.setCustomAnimations(0, 0, 0, 0)
        fragmentTransaction.remove(fragment)
        fragmentTransaction.commitAllowingStateLoss()
    }
}
