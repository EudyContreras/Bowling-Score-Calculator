package com.eudycontreras.bowlingcalculator.utilities

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

class DuplicateBowlerException(message: String? = ""): Exception(message)

class NullParameterException(message: String? = ""): Exception(message)