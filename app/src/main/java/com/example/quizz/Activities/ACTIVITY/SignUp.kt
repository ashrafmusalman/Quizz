package com.example.quizz.Activities.ACTIVITY

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizz.R
import com.example.quizz.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        Log.d("SignUp", "FirebaseAuth instance obtained")

        val signUpBtn: Button = binding.btnSignUp
        signUpBtn.setOnClickListener {
            Log.d("SignUp", "SignUp button clicked")
            setUpSignUp()
        }
// Initialize ProgressBar using view binding
        progressBar = binding.progressBarr

        goToLoginFromSignUpText()
    }

    private fun goToLoginFromSignUpText() {
        binding.tvAskingLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setUpSignUp() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        Log.d("SignUp", "Attempting sign up with email: $email")

        progressBar.visibility = View.VISIBLE

        if (!isValidEmail(email)) {//if emai
            progressBar.visibility = View.GONE

            Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show()
            Log.d("SignUp", "Validation failed: Invalid email")
            return
        }

        if (!isvalidPassword(password)) {
            progressBar.visibility = View.GONE

            Toast.makeText(
                this,
                "password must have at least one special character, one Uppercase on digit",
                Toast.LENGTH_SHORT
            ).show()
            Log.d("SignUp", "Validation failed: Invalid password")
            return
        }

        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            progressBar.visibility = View.GONE

            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            Log.d("SignUp", "Validation failed: Email or Password blank")
            return
        }

        if (password.length < 6) {
            progressBar.visibility = View.GONE

            Toast.makeText(this, "Password cant be less than 6 characters", Toast.LENGTH_SHORT)
                .show()
        }

        if (password != confirmPassword) {
            progressBar.visibility = View.GONE

            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            Log.d("SignUp", "Validation failed: Passwords do not match")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    progressBar.visibility = View.GONE

                    Toast.makeText(this, "SignUp Successful", Toast.LENGTH_SHORT).show()
                    Log.d("SignUp", "SignUp successful")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    progressBar.visibility = View.GONE

                    val errorMessage = task.exception?.message ?: "Unknown error"
                    Toast.makeText(this, "SignUp Failed: $errorMessage", Toast.LENGTH_SHORT).show()
                    Log.e("SignUp", "SignUp failed: $errorMessage")
                }


            }
    }

    private fun isvalidPassword(password: String): Boolean {
        val hasletter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        val hasUpper = password.any { it.isUpperCase() }
        val hasSpecial = password.any { it.isLetterOrDigit() }
        return hasUpper && hasDigit && hasletter && hasSpecial


    }

    private fun isValidEmail(email: String): Boolean {//this is just a function to check if the email is valid or not
        return email.contains("@") && email.contains(".") && email.indexOf('@') < email.lastIndexOf(
            '.'
        )
    }

}