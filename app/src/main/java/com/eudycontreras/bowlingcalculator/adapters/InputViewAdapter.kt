package com.eudycontreras.bowlingcalculator.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.fragments.FragmentCreateBowler


/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class InputViewAdapter(
    private val context: Context?,
    private val createFragmentCreateBowler: FragmentCreateBowler
) : RecyclerView.Adapter<InputViewAdapter.InputViewHolder>() {

    private val items: MutableList<InputViewModel> = MutableList(1) { InputViewModel() }

    init {
        notifyDataSetChanged()
    }

    fun addItem() {
        if(items.isEmpty()) {
            items.add(InputViewModel())
            notifyItemInserted(items.size - 1)
            return
        }
        items.add(InputViewModel())
        notifyItemInserted(items.size)
    }

    fun removeItem(index: Int) {
        items.removeAt(index)
        notifyItemRemoved(index)
    }

    fun getItems(): List<String> {
        return items.filter { it.input.isNotBlank() }.map { it.input }.asReversed()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InputViewHolder {
        return InputViewHolder(LayoutInflater.from(context).inflate(R.layout.dialog_create_bowler_item, parent, false))
    }

    override fun onBindViewHolder(holder: InputViewHolder, position: Int) {
        holder.performBinding(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class InputViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val input: EditText = view.findViewById(R.id.createDialogInput)
        private val delete: View = view.findViewById(R.id.createDialogInputDelete)

        init {
            registerListeners()
        }

        private fun registerListeners() {
            createFragmentCreateBowler.assignInteraction(delete)

            delete.setOnClickListener {
                removeItem(layoutPosition)
            }

            input.doOnTextChanged { text, _, _, _ ->
                text?.let {
                    items[layoutPosition].input = it.toString()
                }
            }
        }

        internal fun performBinding(model: InputViewModel) {
            input.setText(model.input)
            input.requestFocus()
        }
    }

    internal data class InputViewModel(var input: String = "")
}
