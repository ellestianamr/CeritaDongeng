package com.azhar.dongeng.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azhar.dongeng.R
import com.azhar.dongeng.utils.Constant.COLLECTION_DATABASE
import com.azhar.dongeng.utils.Constant.KEY_ID
import com.azhar.dongeng.utils.Constant.KEY_LOGIN
import com.azhar.dongeng.utils.Constant.KEY_NAME
import com.azhar.dongeng.utils.Constant.PREFS_NAME
import com.azhar.dongeng.utils.FirebaseHelper
import com.azhar.dongeng.utils.SharedPreference

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar

    private val db = FirebaseHelper()
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.editTextEmail)
        etPassword = findViewById(R.id.editTextPassword)
        btnLogin = findViewById(R.id.cirLoginButton)
        progressBar = findViewById(R.id.log_progress_bar)

        sharedPref = SharedPreference.initPref(this@LoginActivity, PREFS_NAME)

        btnLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Data cannot be empty", Toast.LENGTH_SHORT).show()
        } else {
            showLoading(true)
            db.loginUser(COLLECTION_DATABASE, email, password) { success, message, id, name ->
                if (success) {
                    SharedPreference.apply {
                        setBooleanPref(KEY_LOGIN, true, applicationContext)
                        setStringPref(KEY_ID, id.toString(), applicationContext)
                        setStringPref(KEY_NAME, name.toString(), applicationContext)
                    }
                    showLoading(false)
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    val i = Intent(this, MainActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(i)
                    finish()
                } else {
                    showLoading(false)
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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

    fun viewRegisterClicked(view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    override fun onStart() {
        super.onStart()
        val isLogin = sharedPref.getBoolean(KEY_LOGIN, false)
        if (isLogin) {
            val i = Intent(this, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(i)
            finish()
        }
    }
}