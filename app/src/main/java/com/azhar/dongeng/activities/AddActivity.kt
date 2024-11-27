package com.azhar.dongeng.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.azhar.dongeng.R
import com.azhar.dongeng.model.ModelMain
import com.azhar.dongeng.utils.Constant.KEY_NAME
import com.azhar.dongeng.utils.Constant.PREFS_NAME
import com.azhar.dongeng.utils.FirebaseHelper
import com.azhar.dongeng.utils.SharedPreference

class AddActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    private lateinit var etTitle: EditText
    private lateinit var etFile: EditText
    private lateinit var txtPrimary: TextView

    private lateinit var btnSubmit: Button
    private lateinit var progressBar: ProgressBar

    private val db = FirebaseHelper()
    private var modelMain: ModelMain? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val i = intent
        if (i != null) modelMain =
            i.getSerializableExtra(DetailActivity.DETAIL_DONGENG) as ModelMain

        etTitle = findViewById(R.id.editTextTitle)
        etFile = findViewById(R.id.editTextFile)
        btnSubmit = findViewById(R.id.btnSubmit)
        progressBar = findViewById(R.id.add_progress_bar)
        txtPrimary = findViewById(R.id.txt_primary)

        var editData = false
        if (modelMain != null) {
            etTitle.setText(modelMain?.strJudul)
            etFile.setText(modelMain?.strCerita)
            editData = true
            txtPrimary.text = "Edit Cerita Dongeng"
        }

        btnSubmit.setOnClickListener {
            val textTitle = etTitle.text.toString().trim()
            val textStory = etFile.text.toString().trim()

            if (textTitle.isNotEmpty() && textTitle.isNotEmpty()) {
                showLoading(true)
                if (!editData) {
                    db.insertDataToFirebase(textTitle, textStory) { success, message ->
                        if (success) {
                            showLoading(false)
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                        } else {
                            showLoading(false)
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    db.editData(modelMain!!.id, textTitle, textStory) { success, message ->
                        println(modelMain!!.id)
                        if (success) {
                            showLoading(false)
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                        } else {
                            showLoading(false)
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Field tidak boleh kosong!", Toast.LENGTH_SHORT).show()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}