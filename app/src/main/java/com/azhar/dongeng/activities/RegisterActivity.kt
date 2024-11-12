package com.azhar.dongeng.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azhar.dongeng.R
import com.azhar.dongeng.utils.Constant.COLLECTION_DATABASE
import com.azhar.dongeng.utils.FirebaseHelper
import java.util.UUID

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar

    private val db = FirebaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.etRegName)
        etEmail = findViewById(R.id.etRegEmail)
        etPassword = findViewById(R.id.etRegPassword)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.reg_progress_bar)

        btnRegister.setOnClickListener {
            register()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")
        return emailRegex.matches(email)
    }

    private fun register() {
        val uniqueId = UUID.randomUUID().toString()
        val name = etName.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if (name.isEmpty() && email.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Data cannot be empty", Toast.LENGTH_SHORT).show()
        } else if (!isValidEmail(email)) {
            Toast.makeText(this, "$email not a valid email address", Toast.LENGTH_SHORT).show()
        } else {
            showLoading(true)
            db.checkEmailExists(COLLECTION_DATABASE, email) { exists ->
                if (exists) {
                    showLoading(false)
                    Toast.makeText(this, "Email has been registered", Toast.LENGTH_SHORT).show()
                } else {
                    val userData = mapOf(
                        "id" to uniqueId,
                        "name" to name,
                        "email" to email,
                        "password" to password
                    )

                    db.registerUser(COLLECTION_DATABASE, uniqueId, userData) { success, message ->
                        if (success) {
                            showLoading(false)
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                        } else {
                            showLoading(false)
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    fun viewLoginClicked(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}