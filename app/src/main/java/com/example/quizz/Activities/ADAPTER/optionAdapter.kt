package com.cheezycode.quizzed.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.quizz.Activities.DataClass.Questions
import com.example.quizz.R

class OptionAdapter(val context: Context, val question: Questions,) :
    RecyclerView.Adapter<OptionAdapter.OptionViewHolder>() {

    private var options: List<String> =
        listOf(question.option1, question.option2, question.option3, question.option4)

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var optionView: TextView = itemView.findViewById(R.id.quizOption)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.option_item, parent, false)
        return OptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val option = options[position]

        // Bind the option text to the TextView
        holder.optionView.text = option

        // Set the background based on the selection status
        if (question.userAnswer == option) {//this line checks if the currently displayed option (option) is the  one the user selected (question.userAnswer). If they match, it means this option was chosen
            // by the user, so it should be highlighted. If they don't match, the option should appear normally without any special highlighting.
            holder.itemView.setBackgroundResource(R.drawable.option_item_selected_bg) // Selected background
        } else {

            holder.itemView.setBackgroundResource(R.drawable.option_item_bg)
        }

        // Handle item click
        holder.itemView.setOnClickListener {
            // Update the selected answer
            question.userAnswer = option
            // Notify the adapter that the data set has changed
            notifyDataSetChanged() // This will trigger the RecyclerView to redraw all the items, updating their appearance
            onOptionClick?.invoke(option.toString())
        }
    }


    override fun getItemCount(): Int {
        return options.size
    }

    var onOptionClick: ((String) -> Unit)? = null
}