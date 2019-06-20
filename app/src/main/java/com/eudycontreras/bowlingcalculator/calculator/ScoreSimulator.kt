package com.eudycontreras.bowlingcalculator.calculator


import com.eudycontreras.bowlingcalculator.MAX_POSSIBLE_SCORE_GAME
import com.eudycontreras.bowlingcalculator.calculator.controllers.StateController
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.State
import com.eudycontreras.bowlingcalculator.utilities.Property

/******************************************************************
 *
 *
 *
 * @author Eudy Contreras.
 * @since 6/20/19
 ******************************************************************/

sealed class ScoreSimulator {

    companion object {
        fun getPossibleScore(bowler: Bowler): Int {

            val stateController = StateController.getInstance()

            val accumulator = Property(MAX_POSSIBLE_SCORE_GAME)

            simulate(stateController.createState(bowler), accumulator)

            return accumulator.get()
        }

        private fun simulate(state: State, accumulator: Property<Int>? = null) {

            for ((index, data) in state.frameScores) {

            }
        }
    }
}