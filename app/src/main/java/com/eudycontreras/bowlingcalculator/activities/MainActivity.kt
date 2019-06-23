package com.eudycontreras.bowlingcalculator.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.components.controllers.*
import com.eudycontreras.bowlingcalculator.utilities.BowlerListener
import com.eudycontreras.bowlingcalculator.utilities.Bowlers
import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_GRACE_PERIOD
import com.eudycontreras.bowlingcalculator.utilities.extensions.app
import com.eudycontreras.bowlingcalculator.utilities.extensions.show
import com.eudycontreras.bowlingcalculator.utilities.runAfterMain
import kotlinx.android.synthetic.main.activity_main.*




/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class MainActivity : AppCompatActivity() {

    private lateinit var scoreController: ScoreController

    private lateinit var skeletonController: SkeletonViewController
    private lateinit var loaderController: LoaderViewController
    private lateinit var framesController: FramesViewController
    private lateinit var actionController: ActionViewController
    private lateinit var statsController: StatsViewController
    private lateinit var tabsController: TabsViewController

    private var created = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.eudycontreras.bowlingcalculator.R.layout.activity_main)

        setSupportActionBar(toolbar as Toolbar)

        initControllers()
    }

    private fun initControllers() {

        scoreController = ScoreController(this)

        skeletonController = SkeletonViewController(this, scoreController)
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

    private fun onStorageEmpty() {
        tabsController.requestTab{
            val activeTab = tabsController.getActive()
            app.persistenceManager.saveActiveTab(activeTab)
            scoreController.initCalculator(it, activeTab)
        }
    }

    private fun onStorageFull() {
        val bowlers = app.persistenceManager.getBowlers()
        val activeTab = app.persistenceManager.activeTab

        bowlers.observe(this, Observer {

            if (it.isEmpty()) {
                onStorageEmpty()
                return@Observer
            }

            loaderController.hideLoader()
            scoreController.initCalculator(it, activeTab)
            tabsController.addTabs(it, activeTab)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        saveCurrentState(scoreController.bowlers)
    }

    override fun onPause() {
        super.onPause()
        saveCurrentState(scoreController.bowlers)
    }

    fun saveCurrentState(bowlers: Bowlers, listener: BowlerListener = null) {
        app.persistenceManager.saveActiveTab(scoreController.activeTab)
        app.persistenceManager.saveBowlers(bowlers, listener)
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
