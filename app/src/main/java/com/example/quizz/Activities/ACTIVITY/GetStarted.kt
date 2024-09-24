package com.example.quizz.Activities.ACTIVITY

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizz.R
import com.example.quizz.databinding.ActivityGetStartedBinding
import com.google.firebase.auth.FirebaseAuth

class getStarted : AppCompatActivity() {
    lateinit var binding: ActivityGetStartedBinding

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)
            //change the color of the status bar
        window.statusBarColor = resources.getColor(R.color.colorPrimaryButtonBg, theme)
         //change the color of the icon of the status bar
        this.getWindow().getDecorView().getWindowInsetsController()
            ?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


//this code allow to ensure that if the user is already registered then , getstarted activity wont be shown
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isRegistered = sharedPreferences.getBoolean("isRegistered", false)

        if (isRegistered) {
            // User is registered, skip getStarted activity and go directly to MainActivity
            Toast.makeText(this, "welcome back", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, splash::class.java)
            startActivity(intent)
            finish()
            return
        }

        val firebase=FirebaseAuth.getInstance()
       val currentUser= firebase.currentUser
if (currentUser!=null){
    startActivity(Intent(this,MainActivity::class.java))
    finish()
}
        else{
            startActivity(Intent(this,Login::class.java))
        }
        getStartedBtnClick()

    }


    // click of the getStarted button
    private fun getStartedBtnClick() {

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isRegistered", true)
        editor.apply()
        binding.getStartedBtn.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
//
//    override fun onStart() {
//        super.onStart()
//
//
//    }

}