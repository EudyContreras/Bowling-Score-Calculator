package com.eudycontreras.bowlingcalculator.utilities

import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

typealias BowlerListener = ((names: List<Bowler>) -> Unit)?

typealias Bowlers = List<Bowler>

typealias Frames = List<Frame>