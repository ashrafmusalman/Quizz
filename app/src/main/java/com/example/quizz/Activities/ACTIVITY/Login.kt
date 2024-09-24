package com.example.quizz.Activities.ACTIVITY

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizz.R
import com.example.quizz.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize ProgressBar
        progressBar = findViewById(R.id.progressBar)

        // Handle edge-to-edge display and padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Check if user is already logged in
       // checkIfUserLoggedIn()

        // Set up sign-in button click listener
        val signInBtn = findViewById<Button>(R.id.loginBtn)
        signInBtn.setOnClickListener {
            setUpSignIn()
        }

        // Navigate to Sign Up screen
        goToSignUpFromLoginText()
    }

    private fun checkIfUserLoggedIn() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            // User is already logged in, redirect to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setUpSignIn() {
        progressBar.visibility = View.VISIBLE

        val email = binding.logInEtEmail.text.toString()
        val password = binding.loginEtPassword.text.toString()

        if (!isValidEmail(email)) {
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show()
            return
        } else if (email.isBlank() || password.isBlank()) {
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        // Perform sign-in with Firebase Auth
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            progressBar.visibility = View.GONE
            if (task.isSuccessful) {
                // Successful login
                Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".") && email.indexOf('@') < email.lastIndexOf('.')
    }

    private fun goToSignUpFromLoginText() {
        binding.tvAskingSignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        }
    }


    override fun onStart() {
        super.onStart()
        checkIfUserLoggedIn()
    }

}
