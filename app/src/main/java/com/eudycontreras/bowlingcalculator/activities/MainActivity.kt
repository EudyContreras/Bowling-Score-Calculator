package com.eudycontreras.bowlingcalculator.activities

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.eudycontreras.bowlingcalculator.MAX_POSSIBLE_SCORE_GAME
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.components.controllers.ActionViewController
import com.eudycontreras.bowlingcalculator.components.controllers.FramesViewController
import com.eudycontreras.bowlingcalculator.components.controllers.StatsViewController
import com.eudycontreras.bowlingcalculator.extensions.app
import com.eudycontreras.bowlingcalculator.extensions.getComputedScore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var scoreController: ScoreController

    private lateinit var framesController: FramesViewController
    private lateinit var actionController: ActionViewController
    private lateinit var statsController: StatsViewController

    private lateinit var bowler: Bowler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        initControllers()
    }

    private fun initControllers() {
        bowler = app.storage.bowler
        scoreController = ScoreController(this.bowler)

        framesController = FramesViewController(this, scoreController)
        actionController = ActionViewController(this, scoreController)
        statsController = StatsViewController(this, scoreController)

        framesController.createFrames(this.bowler.frames)
        scoreController.onScoreUpdated(this.bowler, this.bowler.getCurrentFrame(), this.bowler.frames, this.bowler.frames.getComputedScore(), MAX_POSSIBLE_SCORE_GAME)
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({
            actionController.revealPins()
            framesController.revealFrames()
        }, 800)
    }

    override fun onDestroy() {
        super.onDestroy()
        app.saveBowler(bowler)
    }

    override fun onPause() {
        super.onPause()
        app.saveBowler(bowler)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
