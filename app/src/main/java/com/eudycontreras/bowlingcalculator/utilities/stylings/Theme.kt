package com.eudycontreras.bowlingcalculator.utilities.stylings

import android.content.res.ColorStateList


data class Theme(
    var primary: ColorStateList,
    var primaryLight: ColorStateList,
    var primaryDark: ColorStateList,

    var accent: ColorStateList?,
    var accentLight: ColorStateList?,
    var accentDark: ColorStateList?
)