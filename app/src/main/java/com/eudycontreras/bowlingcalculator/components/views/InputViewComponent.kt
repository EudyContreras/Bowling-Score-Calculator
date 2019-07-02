package com.eudycontreras.bowlingcalculator.components.views

import android.os.Handler
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.doOnLayout
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.components.controllers.InputViewController
import com.eudycontreras.bowlingcalculator.listeners.AnimationListener
import com.eudycontreras.bowlingcalculator.listeners.BackPressedListener
import com.eudycontreras.bowlingcalculator.utilities.Action
import com.eudycontreras.bowlingcalculator.utilities.extensions.AddChangeListener
import com.eudycontreras.bowlingcalculator.utilities.extensions.dp
import com.eudycontreras.bowlingcalculator.utilities.extensions.hideInput
import com.eudycontreras.bowlingcalculator.utilities.extensions.showInput
import kotlinx.android.synthetic.main.activity_main.*

class InputViewComponent(
    private val context: MainActivity,
    val controller: InputViewController
): ViewComponent, BackPressedListener {

    private val parentView: View? = context.inputNameArea

    private val nameInputContainer: LinearLayout? = parentView?.findViewById(R.id.inputRenameContainer)

    private val nameInput: EditText? = parentView?.findViewById(R.id.inputRename)

    private val saveName: FrameLayout? = parentView?.findViewById(R.id.saveRename)

    private val saveNameIcon: View? = parentView?.findViewById(R.id.saveRenameIcon)

    private val handler: Handler = Handler()

    @Volatile private var revealed: Boolean = false

    private val animDuration: Long = 250

    private var height: Float = 0f
    private var depth: Float = 0f

    init {
        setDefaultValues()
        registerListeners()
    }

    override fun setDefaultValues() {
        context.addBackPressListeners(this)

        parentView?.doOnLayout {
            height = it.height.toFloat()
            depth = it.translationZ

            it.translationY = -height
            it.translationZ = depth - 4.dp
        }
    }

    override fun registerListeners() {
        saveName?.setOnClickListener {

        }

        nameInput?.AddChangeListener(
            onChange = {
                if (revealed) {
                    handler.removeCallbacksAndMessages(null)
                }
            },
            onAfterChange = {

            }
        )
    }

    override fun assignInteraction(view: View?) {
        parentView?.let {

        }
    }

    override fun onBackPressed() {
        if (revealed) {
            concealInputContainer(animDuration) {
                revealed = false
            }
        }
    }

    override fun disallowExit(): Boolean {
       return revealed
    }

    private val runnable = {
        if (revealed) {
            concealInputContainer(animDuration) {
                revealed = false
            }
        }
    }

    fun requestRename(bowlerId: Long, bowlerName: String) {
        nameInput?.setText(bowlerName)
        nameInput?.setSelection(bowlerName.length)
        revealInputContainer(animDuration){
            revealed = true
            handler.postDelayed(runnable, controller.concealDelay)
        }
    }

    private fun revealInputContainer(duration: Long, endAction: Action) {
        parentView?.let {
            nameInput?.requestFocus()
            context.showInput()

            var listener: AnimationListener? = null

            if (endAction != null) {
                listener = AnimationListener(onEnd = endAction)
            }

            it.animate()
                .setInterpolator(DecelerateInterpolator())
                .translationZ(depth)
                .translationY(0f)
                .setDuration(duration)
                .setListener(listener)
                .start()
        }
    }

    private fun concealInputContainer(duration: Long, endAction: Action) {
        parentView?.let {
            nameInput?.clearFocus()
            context.hideInput()
            var listener: AnimationListener? = null

            if (endAction != null) {
                listener = AnimationListener(onEnd = endAction)
            }

            it.animate()
                .setInterpolator(AccelerateInterpolator())
                .translationZ(depth - 4.dp)
                .translationY(-height)
                .setDuration(duration)
                .setListener(listener)
                .start()
        }
    }
}