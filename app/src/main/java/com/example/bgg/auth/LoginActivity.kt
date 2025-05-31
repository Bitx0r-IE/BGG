package com.example.bgg.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bgg.DAO.UserDao
import com.example.bgg.Database.AppDatabase
import com.example.bgg.MainActivity
import com.example.bgg.R
import kotlinx.coroutines.launch
import kotlin.math.log

class LoginActivity: AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        val useremailInput = findViewById<EditText>(R.id.editEmail)
        val passwordInput = findViewById<EditText>(R.id.editPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)
        val registerText = findViewById<TextView>(R.id.textRegister)



        loginButton.setOnClickListener {
            val email = useremailInput.text.toString()
            val password = passwordInput.text.toString()

            lifecycleScope.launch {
                val user = userDao.getUserByEmail(email)
                if (user != null && user.password == password) {
                    val sharedPref = getSharedPreferences("bgg_prefs", MODE_PRIVATE)
                    sharedPref.edit().putInt("userId", user.id).apply() // Se que es más optimo y seguro hacer una call, a getuserby pero por no rizar mas el rizo dejo el sharedData
                    sharedPref.edit().putString("userName", user.name).apply()
                    sharedPref.edit().putString("userEmail", user.email).apply()
                    sharedPref.edit().putString("userPass", user.password).apply()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }

        registerText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}