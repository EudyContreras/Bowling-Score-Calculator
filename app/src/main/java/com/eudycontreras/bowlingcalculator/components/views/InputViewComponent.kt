package com.eudycontreras.bowlingcalculator.components.views

import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.view.doOnLayout
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.components.controllers.InputViewController
import com.eudycontreras.bowlingcalculator.listeners.AnimationListener
import com.eudycontreras.bowlingcalculator.listeners.BackPressedListener
import com.eudycontreras.bowlingcalculator.utilities.Action
import com.eudycontreras.bowlingcalculator.utilities.extensions.*
import com.github.ybq.android.spinkit.SpinKitView
import kotlinx.android.synthetic.main.activity_main.*




class InputViewComponent(
    private val context: MainActivity,
    val controller: InputViewController
): ViewComponent, BackPressedListener {

    private val doneIcon: Drawable = context.drawable(R.drawable.ic_done)
    private val saveIcon: Drawable = context.drawable(R.drawable.ic_save_score)

    private val parentView: View? = context.inputNameArea

    private val nameInput: EditText? = parentView?.findViewById(R.id.inputRename)
    private val saveNameAction: FrameLayout? = parentView?.findViewById(R.id.saveRename)
    private val saveNameIcon: View? = parentView?.findViewById(R.id.saveRenameIcon)
    private val saveLoader: SpinKitView? = parentView?.findViewById(R.id.saveRenameLoader)

    private val handler: Handler = Handler()

    private val renameInfo: RenameInfo = RenameInfo()

    private val animDuration: Long = 250

    private var height: Float = 0f
    private var depth: Float = 0f

    data class RenameInfo(
        var bowlerId: Long = 0L,
        var oldName: String = "",
        var newName: String = ""
    )

    init {
        setDefaultValues()
        registerListeners()
        assignInteraction(null)
    }

    override fun setDefaultValues() {
        saveLoader?.hide()
        context.addBackPressListeners(this)

        parentView?.doOnLayout {
            height = it.height.toFloat()
            depth = it.translationZ

            it.translationY = -height
            it.translationZ = depth - 4.dp
        }
    }

    override fun registerListeners() {
        nameInput?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveNewName(nameInput.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }

        nameInput?.setOnKeyListener { _, keyCode, keyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                saveNewName(nameInput.text.toString())
                return@setOnKeyListener  true
            }
            return@setOnKeyListener  false
        }

        saveNameAction?.setOnClickListener {
            saveNewName(nameInput?.text.toString())
        }

        nameInput?.AddChangeListener(
            onChange = {
                if (controller.revealed) {
                    handler.removeCallbacksAndMessages(null)
                }
            }
        )
    }

    override fun assignInteraction(view: View?) {
        saveNameAction?.addTouchAnimation(
            clickTarget = null,
            scale = 0.90f,
            depth = 0f,
            interpolatorPress = DecelerateInterpolator(),
            interpolatorRelease = OvershootInterpolator()
        )
    }

    override fun onBackPressed() {
        if (controller.revealed) {
            concealInputContainer(animDuration) {
                controller.revealed = false
            }
        }
    }

    override fun disallowExit(): Boolean {
       return controller.revealed
    }

    private val runnable = {
        if (controller.revealed) {
            concealInputContainer(animDuration) {
                controller.revealed = false
            }
        }
    }

    fun animateSave(duration: Long) {

    }

    private fun saveNewName(newName: String) {
        if (newName.contentEquals(renameInfo.oldName)) {
            return
        }

        renameInfo.newName = newName

        saveNameIcon?.let { view ->
            val switchIcon: Action = {
                saveLoader?.show(150)
                controller.saveNewName(renameInfo) {
                    saveLoader?.hide(150)
                    onNameSaved(view, renameInfo)
                }
            }

            view.animate()
                .setListener(AnimationListener(onEnd = switchIcon))
                .scaleX(0f)
                .scaleY(0f)
                .setDuration(150)
                .start()
        }
    }

    private fun onNameSaved(view: View, info: RenameInfo) {
        handler.postDelayed(runnable, controller.concealDelayMini)

        view.background = doneIcon
        view.animate()
            .setListener(null)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(200)
            .setInterpolator(OvershootInterpolator())
            .start()
    }

    fun requestRename(bowlerId: Long, bowlerName: String) {
        renameInfo.bowlerId = bowlerId
        renameInfo.oldName = bowlerName

        nameInput?.setText(bowlerName)
        nameInput?.setSelection(bowlerName.length)
        saveNameIcon?.background = saveIcon

        revealInputContainer(animDuration){
            controller.revealed = true
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