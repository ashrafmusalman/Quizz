package com.example.quizz.Activities.ADAPTER

import com.example.quizz.Activities.DataClass.Quizz
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizz.Activities.ACTIVITY.QuestionActivity
import com.example.quizz.Activities.Util.ColorPicker
import com.example.quizz.R
import imagePicker

class QuizAdapter(
    val context: Context, val quizzes: List<Quizz>
) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {
    private var filteredQuizList: List<Quizz> = quizzes

    // Filter method to filter the quizzes based on the title
    fun filter(query: String) {
        filteredQuizList = if (query.isEmpty()) {
            quizzes
        } else {
            quizzes.filter { it.title.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged() // Notify the adapter about data changes
    }

    class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle = itemView.findViewById<TextView>(R.id.quizTitle)
        val iconView = itemView.findViewById<ImageView>(R.id.quizIcon)
        val cardContainer = itemView.findViewById<CardView>(R.id.cardContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val iteView = LayoutInflater.from(context)
            .inflate(R.layout.quizz_rv_single_item_design, parent, false)
        return QuizViewHolder(iteView)
    }

    override fun getItemCount(): Int {
        return filteredQuizList.size
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val currentItem = filteredQuizList[position]
        holder.textViewTitle.text = currentItem.title
        Log.d("QuizAdapter", "Title: ${currentItem.title}")

        // Retrieve color from ColorPicker
        val color = Color.parseColor(ColorPicker.getColor())
        holder.cardContainer.setCardBackgroundColor(color)

        // Set the image resource for the icon view (ensure imagePicker is correctly implemented)
        holder.iconView.setImageResource(imagePicker.getImages())

        //if the quiz item are clicked

        holder.itemView.setOnClickListener {
            onOptionClick?.invoke(currentItem.title)
        }
    }

    //to perform the click on the item of the adapter
    var onOptionClick: ((String) -> Unit)? = null
}
