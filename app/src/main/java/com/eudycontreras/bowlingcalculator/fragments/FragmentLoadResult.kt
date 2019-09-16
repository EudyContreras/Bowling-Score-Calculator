package com.eudycontreras.bowlingcalculator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.components.controllers.ActionViewController
import kotlinx.android.synthetic.main.dialog_load_result.view.*

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

class FragmentLoadResult: DialogFragment() {

    var controller: ActionViewController? = null

    private lateinit var loadDialog: ConstraintLayout

    companion object {
        fun instance(controller: ActionViewController): FragmentLoadResult {
            val fragment = FragmentLoadResult()
            fragment.controller = controller
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        loadDialog = inflater.inflate(R.layout.dialog_load_result, container) as ConstraintLayout
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        setDefaultValues()
        registerListeners()

        return loadDialog
    }

    override fun setDefaultValues() {
        assignInteraction(loadDialog.dialogLoadClear)
    }

    override fun registerListeners() {
        loadDialog.dialogLoadClear.setOnClickListener {

        }
    }
}