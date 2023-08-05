package com.example.mainrobot
//class before the code to manage the user login across the app
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var switchTextView: TextView
    private var isRegisterMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        loginButton = findViewById(R.id.actionButton)
        switchTextView = findViewById(R.id.switchTextView)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (isRegisterMode) {
                val confirmPassword = confirmPasswordEditText.text.toString()
                if (password != confirmPassword) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // Perform registration logic here
                registerUser(username, password)
            } else {
                // Perform your login validation here
                val isValidLogin = validateLogin(username, password)

                if (isValidLogin) {
                    // Set the login status
                    val sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("is_logged_in", true)
                    editor.apply()

                    // Start the MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }

        switchTextView.setOnClickListener {
            isRegisterMode = !isRegisterMode
            if (isRegisterMode) {
                confirmPasswordEditText.visibility = View.VISIBLE
                loginButton.text = "Register"
                switchTextView.text = "Already have an account? Login here."
            } else {
                confirmPasswordEditText.visibility = View.GONE
                loginButton.text = "Login"
                switchTextView.text = "Don't have an account? Register here."
            }
        }
    }

    private fun validateLogin(username: String, password: String): Boolean {
        // Implement your login validation logic here
        // Return true if the login is successful, false otherwise
        // For example:
        val validUsername = "admin"
        val validPassword = "123456"

        return username == validUsername && password == validPassword
    }

    private fun registerUser(username: String, password: String) {
        // Implement your registration logic here
        // Add code to create a new user account with the provided username and password
        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
        // You can also automatically log in the user after registration if needed
        // and proceed to the main screen
    }
}

