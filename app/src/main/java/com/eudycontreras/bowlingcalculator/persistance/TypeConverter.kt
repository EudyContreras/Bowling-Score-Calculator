package com.eudycontreras.bowlingcalculator.persistance

import androidx.room.TypeConverter
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.calculator.elements.Roll
import java.util.*


/**
 * Created by eudycontreras.
 */

class TypeConverter {

    @TypeConverter
    fun toState(name: String): Frame.State = Frame.State.valueOf(name)
    @TypeConverter
    fun toString(state: Frame.State): String = state.name

    @TypeConverter
    fun toSkill(name: String): Bowler.SkillLevel = Bowler.SkillLevel.valueOf(name)
    @TypeConverter
    fun toString(skill: Bowler.SkillLevel): String = skill.name

    @TypeConverter
    fun toResult(name: String): Roll.Result = Roll.Result.valueOf(name)
    @TypeConverter
    fun toString(result: Roll.Result): String = result.name

    @TypeConverter
    fun toDate(timestamp: Long): Date = Date(timestamp)
    @TypeConverter
    fun toTimestamp(date: Date): Long = date.time
}