package com.eudycontreras.bowlingcalculator.utilities

import android.graphics.Canvas
import android.graphics.Color
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

typealias AndroidColor = Color

typealias BowlerListener = ((names: List<Bowler>) -> Unit)?

typealias Bowlers = List<Bowler>

typealias Frames = List<Frame>

typealias Action = (()-> Unit)?

typealias DrawDeletegate = ((Canvas, Float, Float, Int, Int) -> Unit)?