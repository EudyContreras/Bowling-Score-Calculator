package com.eudycontreras.bowlingcalculator

import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.utilities.extensions.getComputedScore
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RollResultTests {

    @Test
    fun exampleRollTestOne() {

        val bowler = Bowler()
        bowler.init()
        bowler.reset()

        val strike = 10
        val spare = 6 to 4
        val other = 4 to 0

        bowler.performRoll(strike)

        bowler.performRoll(spare.first)
        bowler.performRoll(spare.second)

        bowler.performRoll(other.first)
        bowler.performRoll(other.second)

        val totalPoints = bowler.getComputedScore()

        assert(totalPoints == 38)
    }

    @Test
    fun exampleRollTestTwo() {

        val bowler = Bowler()

        val strike = 10

        for (counter in 0..12) {
            bowler.performRoll(strike)
        }

        val totalPoints = bowler.getComputedScore()

        assert(totalPoints == 300)
    }
}
