package com.eudycontreras.bowlingcalculator.calculator

import com.eudycontreras.bowlingcalculator.DEFAULT_FRAME_COUNT
import com.eudycontreras.bowlingcalculator.MAX_POSSIBLE_SCORE_GAME
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.calculator.elements.FrameNormal
import com.eudycontreras.bowlingcalculator.calculator.elements.Roll
import com.eudycontreras.bowlingcalculator.calculator.listeners.ScoreStateListener
import com.eudycontreras.bowlingcalculator.extensions.getComputedScore
import com.eudycontreras.bowlingcalculator.extensions.sum
import java.io.Serializable

/**
 * Created by eudycontreras.
 */

sealed class ScoreCalculator(
    var name: String
) : Serializable {

    companion object {

        fun calculate(bowler: Bowler, frames: List<Frame>, listener: ScoreStateListener?) {

            frames.forEach {
                it.bonusPoints = 0
                it.pointsFromPrevious = 0
            }

            for ((index, frame) in frames.withIndex()) {
                if (frame.rolls.isEmpty())
                    continue

                if (frame is FrameNormal) {
                    handleFameCalculation(index, frame, frames)
                }

                if (index > 0) {
                    frame.pointsFromPrevious = frames[index - 1].getTotal(true)
                }
            }

            listener?.onScoreUpdated(
                bowler,
                bowler.getCurrentFrame(),
                frames,
                frames.getComputedScore(),
                MAX_POSSIBLE_SCORE_GAME
            )
        }

        private fun handleFameCalculation(index: Int, frame: Frame, frames: List<Frame>) {

            if (frame.rolls.values.any { it.result == Roll.Result.STRIKE }) {
                val nextTwoRolls = getNextRolls(index, frames, Roll.Result.STRIKE)
                frame.bonusPoints = nextTwoRolls.sum()
            }

            if (frame.rolls.values.any { it.result == Roll.Result.SPARE }) {
                val nextRoll = getNextRolls(index, frames, Roll.Result.SPARE)
                frame.bonusPoints = nextRoll.sum()
            }
        }

        private fun getNextRolls(index: Int, frames: List<Frame>, result: Roll.Result): List<Roll> {
            val rolls: ArrayList<Roll> = ArrayList()

            getRollsForNormalFrame(result, index, rolls, frames)

            return rolls
        }

        private fun getRollsForNormalFrame(
            result: Roll.Result,
            index: Int,
            rolls: ArrayList<Roll>,
            frames: List<Frame>
        ) {
            if (result == Roll.Result.STRIKE) {
                if ((index + 1) < DEFAULT_FRAME_COUNT) {
                    val frame = frames[index + 1]
                    frame.getRollBy(Frame.State.FIRST_CHANCE)?.let { rolls.add(it) }
                    frame.getRollBy(Frame.State.SECOND_CHANCE)?.let { rolls.add(it) }
                }

                if (rolls.size < 2) {
                    if ((index + 2) < DEFAULT_FRAME_COUNT) {
                        val frame = frames[index + 2]
                        frame.getRollBy(Frame.State.FIRST_CHANCE)?.let { rolls.add(it) }
                    }
                }
            } else {
                if ((index + 1) < DEFAULT_FRAME_COUNT) {
                    val frame = frames[index + 1]
                    frame.getRollBy(Frame.State.FIRST_CHANCE)?.let { rolls.add(it) }
                }
            }
        }
    }
}