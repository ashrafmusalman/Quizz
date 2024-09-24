package com.example.quizz.Activities.ACTIVITY

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cheezycode.quizzed.adapters.OptionAdapter
import com.example.quizz.Activities.DataClass.Quizz
import com.example.quizz.R
import com.example.quizz.databinding.ActivityQuestionBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class QuestionActivity : AppCompatActivity() {

    private lateinit var quiz: Quizz
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: ActivityQuestionBinding
    private var index = 0
    private var isDataReady = false
    private var originalBtnNextColor: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        originalBtnNextColor = ContextCompat.getColor(this, R.color.colorAccent) // Replace with your original color resource

        // Apply window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        val title = intent.getStringExtra("Title")

        if (title != null) {
            FirebaseFirestore.getInstance().collection("quizzes")
                .whereEqualTo("title", title)
                .get()
                .addOnSuccessListener { question ->
                    val data = question.toObjects(Quizz::class.java)
                    if (data.isNotEmpty()) {
                        quiz = data[0]
                        isDataReady = true
                        displayQuizData() // Display the first question
                        setupBtnClick() // Ensure button click listeners are set up after data is loaded
                        Log.d("Questions", quiz.questions.values.toString())
                    } else {
                        Toast.makeText(this, "No quiz found for the title", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error in fetching data", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No title provided", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayQuizData() {
        if (isDataReady && quiz.questions.isNotEmpty()) {
            val question = quiz.questions.values.toList()[index] // Get the question at the current index
            val quizTitleTextView = findViewById<TextView>(R.id.description)
            quizTitleTextView.text = question.description // Set the question description

            // Set up RecyclerView with the current question's options
            val recyclerView = findViewById<RecyclerView>(R.id.optionList)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = OptionAdapter(this, question) // Pass the current question

            buttonVisibility() // Update button visibility
        } else {
            Toast.makeText(this, "No question available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBtnClick() {
        // Ensure buttons are correctly initialized
        binding.btnNext.isEnabled = isDataReady
        binding.btnPrevious.isEnabled = isDataReady
        binding.btnSubmit.isEnabled = isDataReady

        binding.btnNext.setOnClickListener {
            if (isDataReady && index < quiz.questions.size - 1) {
                index++
                displayQuizData() // Display the next question based on index
                binding.btnNext.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))

            }
        }

        binding.btnPrevious.setOnClickListener {
            binding.btnSubmit.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))

            if (isDataReady && index > 0) {
                index--
                displayQuizData() // Display the previous question based on index
            }
        }

        binding.btnSubmit.setOnClickListener {
            binding.btnSubmit.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))

            if (isDataReady) {
                Toast.makeText(this, "This is the last question", Toast.LENGTH_SHORT).show()
                binding.btnSubmit.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))

            }
        }
    }

    private fun buttonVisibility() {
        if (!isDataReady) return

        // Hide all buttons initially
        binding.btnPrevious.visibility = View.GONE
        binding.btnNext.visibility = View.GONE
        binding.btnSubmit.visibility = View.GONE

        when (index) {
            0 -> { // First question
                binding.btnNext.visibility = View.VISIBLE
            }
            quiz.questions.size - 1 -> { // Last question
                binding.btnPrevious.visibility = View.VISIBLE
                binding.btnSubmit.visibility = View.VISIBLE
            }
            else -> { // Any question in between
                binding.btnPrevious.visibility = View.VISIBLE
                binding.btnNext.visibility = View.VISIBLE
            }
        }
    }
}
