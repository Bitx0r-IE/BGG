package com.example.bgg.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bgg.DAO.UserDao
import com.example.bgg.Database.AppDatabase
import com.example.bgg.Entities.UserEntity
import com.example.bgg.R
import kotlinx.coroutines.launch

class RegisterActivity: AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        val usernameInput = findViewById<EditText>(R.id.editUsername)
        val useremailInput = findViewById<EditText>(R.id.editEmail)
        val passwordInput = findViewById<EditText>(R.id.editPassword)
        val registerButton = findViewById<Button>(R.id.buttonRegister)

        registerButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val email = useremailInput.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    val user = UserEntity(name = username, email = email, password = password)
                    userDao.insert(user)
                    Toast.makeText(this@RegisterActivity, "Registered", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}