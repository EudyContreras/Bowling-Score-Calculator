package com.eudycontreras.bowlingcalculator.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.eudycontreras.bowlingcalculator.MAX_POSSIBLE_SCORE_GAME
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.components.controllers.ActionViewController
import com.eudycontreras.bowlingcalculator.components.controllers.FramesViewController
import com.eudycontreras.bowlingcalculator.components.controllers.StatsViewController
import com.eudycontreras.bowlingcalculator.extensions.getComputedScore
import com.eudycontreras.bowlingcalculator.persistance.PrimitiveStorage
import com.eudycontreras.bowlingcalculator.persistance.SharedPreferencesStorage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var scoreController: ScoreController

    private lateinit var framesController: FramesViewController
    private lateinit var actionController: ActionViewController
    private lateinit var statsController: StatsViewController

    private lateinit var storage: PrimitiveStorage

    private lateinit var bowler: Bowler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        initControllers()
    }

    private fun initControllers() {
        storage = SharedPreferencesStorage(this)

        bowler = storage.bowler
        scoreController = ScoreController(bowler)

        framesController = FramesViewController(this, scoreController)
        actionController = ActionViewController(this, scoreController)
        statsController = StatsViewController(this, scoreController)

        framesController.createFrames(bowler.frames)
        scoreController.onScoreUpdated(bowler, bowler.getCurrentFrame(), bowler.frames, bowler.frames.getComputedScore(), MAX_POSSIBLE_SCORE_GAME)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        storage.bowler = bowler
    }

    override fun onPause() {
        super.onPause()
        storage.bowler = bowler
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
