package com.eudycontreras.bowlingcalculator.calculator.elements

import com.eudycontreras.bowlingcalculator.DEFAULT_PIN_COUNT


/**
 * A bowling roll made by the bowler. The result of
 * the roll is partially determined by the bowler's skill
 *
 * @property result the result that the roll yielded
 * @constructor Creates a bowling roll
 * @author Eudy Contreras
 */
data class Roll(
    var totalKnockdown: Int = 0,
    var result: Result = Result.UNKNOWN
): Element {

    data class Probability<T : Number>(val min: T, val max: T)

    var frameIndex: Int = 0

    var parentState: Frame.State = Frame.State.FIRST_CHANCE

    var id: Int = 0

    override fun init() { }

    override fun reset() {
        id = 0
        frameIndex = 0
        totalKnockdown = 0
        result = Result.UNKNOWN
        parentState = Frame.State.FIRST_CHANCE
    }

    enum class Result {
        STRIKE {
            override fun getProbability(skill: Bowler.SkillLevel): Probability<Int> {
                return when(skill) {
                    Bowler.SkillLevel.NOVICE -> Probability(
                        min = 0,
                        max = 30
                    )
                    Bowler.SkillLevel.AMATEUR -> Probability(
                        min = 5,
                        max = 50
                    )
                    Bowler.SkillLevel.EXPERIENCED -> Probability(
                        min = 10,
                        max = 70
                    )
                    Bowler.SkillLevel.PROFESSIONAL -> Probability(
                        min = 15,
                        max = 100
                    )
                }
            }
        },
        SPARE {
            override fun getProbability(skill: Bowler.SkillLevel): Probability<Int> {
                return when(skill) {
                    Bowler.SkillLevel.NOVICE -> Probability(
                        min = 10,
                        max = 50
                    )
                    Bowler.SkillLevel.AMATEUR -> Probability(
                        min = 15,
                        max = 60
                    )
                    Bowler.SkillLevel.EXPERIENCED -> Probability(
                        min = 20,
                        max = 80
                    )
                    Bowler.SkillLevel.PROFESSIONAL -> Probability(
                        min = 25,
                        max = 90
                    )
                }
            }
        },
        MISS {
            override fun getProbability(skill: Bowler.SkillLevel): Probability<Int> {
                return when(skill) {
                    Bowler.SkillLevel.NOVICE -> Probability(
                        min = 20,
                        max = 80
                    )
                    Bowler.SkillLevel.AMATEUR -> Probability(
                        min = 15,
                        max = 60
                    )
                    Bowler.SkillLevel.EXPERIENCED -> Probability(
                        min = 10,
                        max = 50
                    )
                    Bowler.SkillLevel.PROFESSIONAL -> Probability(
                        min = 5,
                        max = 30
                    )
                }
            }
        },
        NORMAL {
            override fun getProbability(skill: Bowler.SkillLevel): Probability<Int> {
                return when(skill) {
                    Bowler.SkillLevel.NOVICE -> Probability(
                        min = 5,
                        max = 70
                    )
                    Bowler.SkillLevel.AMATEUR -> Probability(
                        min = 10,
                        max = 80
                    )
                    Bowler.SkillLevel.EXPERIENCED -> Probability(
                        min = 15,
                        max = 80
                    )
                    Bowler.SkillLevel.PROFESSIONAL -> Probability(
                        min = 20,
                        max = 90
                    )
                }
            }
        },
        UNKNOWN {
            override fun getProbability(skill: Bowler.SkillLevel): Probability<Int> {
                return Probability(0, 0)
            }
        };

        /**
         * Returns the probability the result based on the arbitrarily distributed skill level
         * of the player making the throw. Beware that the probabilities are computed
         * upon nondeterministic values
         */
        abstract fun getProbability(skill: Bowler.SkillLevel): Probability<Int>

        companion object {
            fun from(lastRoll: Roll?, pinKnockedCount: Int): Result {
               var result = when(pinKnockedCount) {
                   0 -> MISS
                   in 1..9 -> NORMAL
                   DEFAULT_PIN_COUNT -> STRIKE
                   else -> UNKNOWN
                }

                lastRoll?.let {
                    if ((pinKnockedCount + it.totalKnockdown) == DEFAULT_PIN_COUNT) {
                        if (result != MISS && it.result != STRIKE) {
                            result = SPARE
                        } else {

                        }
                    }
                }
                return result
            }
        }
    }
}