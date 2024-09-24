package com.example.quizz.Activities.DataClass

import com.example.quizz.Activities.DataClass.Questions
import java.io.Serializable

data class Quizz(
    var id: String = "",
    var title: String = "",
    var questions: MutableMap<String, Questions> = mutableMapOf()
)
