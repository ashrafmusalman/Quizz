package com.example.quizz.Activities.DataClass

data class Questions(
    var description: String = "",
    var option1:String="" ,
    var option2:String="" ,
    var option3:String="" ,
    var option4:String="" ,
    var userAnswer:String="", // The user's selected option (e.g., "A")
    var correctAnswerKey: String = "", // The key of the correct option (e.g., "C")
)
