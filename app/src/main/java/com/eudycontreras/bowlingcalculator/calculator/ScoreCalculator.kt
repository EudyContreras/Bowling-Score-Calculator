package com.eudycontreras.bowlingcalculator.calculator

import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.calculator.elements.FrameNormal
import com.eudycontreras.bowlingcalculator.calculator.elements.Roll
import com.eudycontreras.bowlingcalculator.calculator.listeners.ScoreStateListener
import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_FRAME_CHANCES
import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_FRAME_COUNT
import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_PIN_COUNT
import com.eudycontreras.bowlingcalculator.utilities.ZERO
import com.eudycontreras.bowlingcalculator.utilities.extensions.clone
import com.eudycontreras.bowlingcalculator.utilities.extensions.next
import com.eudycontreras.bowlingcalculator.utilities.extensions.previous
import com.eudycontreras.bowlingcalculator.utilities.extensions.sum

/***********************************************************************
 *
 * #############     ####     ####     ##########      #####      #####
 * #############     ####     ####     ############     ####      ####
 * ####              ####     ####     ####     ####     ####    ####
 * ####              ####     ####     ####      ####     ####  ####
 * #############     ####     ####     ####      ####      ########
 * #############     ####     ####     ####      ####        ####
 * ####              ####     ####     ####      ####        ####
 * ####              #####   #####     ####     ####         ####
 * #############      ###########      ############          ####
 * #############        #######        ##########            ####
 *
 * ######################   Class description  #########################
 *
 * Class in charge of performing the calculations that determine the
 * score achieved upon each play performed by the bowler.
 *
 * ################   Description of the algorithm:   ##################
 *
 * 1: get all frames
 * 2: reset the bonus points and the added points from previous
 * 3: loop through each frame
 * 4: if the frame being looped has rolls
 * 5: and if the frame is a normal frame
 * 6: if the current frame in the loop has a strike
 * 7: if there are two next rolls
 * 8: add the points achieved on the next two rolls as a bonus
 * 9: else add the points from next roll
 * 10: else if the frame has a spare
 * 11: if there is a next roll
 * 12: add the points achieved by the next roll as a bonus
 * 13: if the current frame in the loop is not the first frame
 * 14: add the points achieved by the last frame to the current frame
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since June 21 2019
 *
 *********************************************************************/

sealed class ScoreCalculator {

    companion object {

        /**
         * Calculates the result/points achieved by this bowler. The
         * results uses backtracking and forward tracking in order to determine
         * the actual score
         *
         * @param bowler The bowler whose result is to be calculated
         * @param listener The listener which listens to the calculated result
         * @param simulated The flag which lets the algorithm know whether
         * it is called by a simulation
         */
        fun calculateScore(bowler: Bowler, listener: ScoreStateListener?, simulated: Boolean = false) {

            val frames = bowler.frames

            frames.forEach {
                it.bonusPoints = ZERO
                it.pointsFromPrevious = ZERO
            }

            for ((index, frame) in frames.withIndex()) {
                if (frame.rolls.isEmpty())
                    continue

                if (frame is FrameNormal) {
                    handleFrameCalculation(index, frames)
                }

                if (index > ZERO) {
                    frame.pointsFromPrevious = frames[index.previous()].getTotal(true)
                }
            }

            if (simulated) {
                return
            }

            val totalScore = getTotalScore(bowler)
            val possibleScore = getPossibleScore(bowler)

            listener?.onScoreUpdated(bowler, totalScore, possibleScore)
        }

        /**
         * Calculates the score to be given to each frame based
         * on index and other criteria such as frame roll results
         *
         * @param index The index to be calculated
         * @param frames The frames being checked upon calculation
         */
        private fun handleFrameCalculation(
            index: Int,
            frames: List<Frame>
        ) {
            val frame = frames[index]

            if (frame.rolls.values.any { it.result == Roll.Result.STRIKE }) {
                val rolls = getNextRolls(Roll.Result.STRIKE, index, frames)

                frame.bonusPoints = rolls.sum()
                return
            }

            if (frame.rolls.values.any { it.result == Roll.Result.SPARE }) {
                val rolls = getNextRolls(Roll.Result.SPARE, index, frames)

                frame.bonusPoints = rolls.sum()
            }
        }

        /**
         * Attempts to retrieved the next rolls performed by the user given the
         * specified criteria. When the frame currently being check contains a strike
         * the function will attempt to retrieve the next two rolls. Otherwise only
         * the next roll will be retrieved if available
         *
         * @param result The result criteria which determines how to retrieve the rolls
         * @param index The current index of the frame being checked
         * @param frames The frames from which the rolls are extracted
         *
         * @return The list of rolls preceding the former
         */
        private fun getNextRolls(
            result: Roll.Result,
            index: Int,
            frames: List<Frame>
        ): List<Roll> {
            val rolls: ArrayList<Roll> = ArrayList()

            if (result == Roll.Result.STRIKE) {
                if ((index.next()) < DEFAULT_FRAME_COUNT) {
                    val frame = frames[index.next()]

                    frame.getRollBy(Frame.State.FIRST_CHANCE)?.let { rolls.add(it) }
                    frame.getRollBy(Frame.State.SECOND_CHANCE)?.let { rolls.add(it) }
                }

                if (rolls.size < DEFAULT_FRAME_CHANCES) {
                    if (index.next(shift = 2) < DEFAULT_FRAME_COUNT) {
                        val frame = frames[index.next(shift = 2)]

                        frame.getRollBy(Frame.State.FIRST_CHANCE)?.let { rolls.add(it) }
                    }
                }

                return rolls
            }

            if ((index.next()) < DEFAULT_FRAME_COUNT) {
                val frame = frames[index.next()]

                frame.getRollBy(Frame.State.FIRST_CHANCE)?.let { rolls.add(it) }
            }

            return rolls
        }

        /**
         * Simulates the result of a bowlers game given that the bowler
         * throws every best throw possible after its current state.
         * This is used to calculateScore the best possible achievable result
         * for said bowler
         *
         * @param reference The reference bowler to be simulated
         *
         * @return the final total score for said bowler after simulation
         */
        private fun simulateWith(reference: Bowler): Int {

            //TODO(Find out how to make it work with the last frames)

            val bowler = reference.clone()
            var counter = bowler.currentFrameIndex

            while (counter < DEFAULT_FRAME_COUNT - 1) {
                val current = bowler.getCurrentFrame()

                if (!current.isCompleted) {
                    val remaining = current.pinUpCount()
                    bowler.performRoll(remaining, null, true)
                    continue
                }

                bowler.performRoll(DEFAULT_PIN_COUNT, null, true)
                counter ++
            }
            return getTotalScore(bowler)
        }

        /**
         * Returns the total score for the given bowler
         *
         * @param bowler The bowler's whose score is to be calculated
         *
         * @return The total sum of the score achieved on each frame
         */
        fun getTotalScore(bowler: Bowler): Int {
            return bowler.frames.sumBy { it.getTotal(false) }
        }

        /**
         * Returns the total possible score the given bowler
         * can achieved base on the current state of the bowlers game
         *
         * @param bowler The bowler's whose best possible score is to be calculated
         *
         * @return The total simulated best possible score
         */
        fun getPossibleScore(bowler: Bowler): Int {
            return simulateWith(bowler)
        }
    }
}