package com.eudycontreras.bowlingcalculator.utilities

import android.graphics.Color
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */
typealias AndroidColor = Color

typealias BowlerListener = ((names: List<Bowler>) -> Unit)?

typealias Bowlers = List<Bowler>

typealias Action = (()-> Unit)?