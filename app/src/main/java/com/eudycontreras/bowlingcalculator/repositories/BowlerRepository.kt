package com.eudycontreras.bowlingcalculator.repositories

import androidx.lifecycle.LiveData
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Result

/**
 * Created by eudycontreras.
 */

interface BowlerRepository {

    fun saveBowler(bowler: Bowler)

    fun updateBowler(bowler: Bowler)

    fun saveBowlers(bowlers: List<Bowler>)

    fun getBowlers(bowlerIds: LongArray): LiveData<List<Bowler>>

    fun getBowlers(result: Result): List<Bowler>

    fun getDefaultBowler(): LiveData<Bowler>

    fun deleteBowler(bowler: Bowler)

    fun deleteAll(result: Result)

    fun deleteAll()

}