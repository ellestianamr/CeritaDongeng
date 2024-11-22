package com.azhar.dongeng.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azhar.dongeng.R
import com.azhar.dongeng.adapter.MainAdapter
import com.azhar.dongeng.model.ModelMain
import com.azhar.dongeng.utils.Constant.KEY_ID
import com.azhar.dongeng.utils.Constant.PREFS_NAME
import com.azhar.dongeng.utils.FirebaseHelper
import com.azhar.dongeng.utils.SharedPreference
import java.util.Collections

class FavoriteActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: Toolbar
    private lateinit var rvListDongeng: RecyclerView
    private lateinit var mainAdapter: MainAdapter
    private var listFavorite: ArrayList<ModelMain> = ArrayList()
    private val db = FirebaseHelper()
    private lateinit var sharedPref: SharedPreferences
    private var idUser: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        toolbar = findViewById(R.id.toolbarFavorite)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        sharedPref = SharedPreference.initPref(applicationContext, PREFS_NAME)
        idUser = sharedPref.getString(KEY_ID, "") ?: ""

        progressBar = findViewById(R.id.fav_progress_bar)
        rvListDongeng = findViewById(R.id.rvListDongeng)
        rvListDongeng.layoutManager = LinearLayoutManager(this)
        rvListDongeng.setHasFixedSize(true)
        getDataDongeng()
    }

    private fun getDataDongeng() {
        showLoading(true)
        db.getAllDataFavorite(idUser) { success, dataList ->
            if (success) {
                listFavorite = dataList as ArrayList<ModelMain>
                if (listFavorite.size > 0) {
                    rvListDongeng.visibility = View.VISIBLE
                    mainAdapter = MainAdapter(this, listFavorite)
                    rvListDongeng.adapter = mainAdapter
                    Collections.sort(listFavorite, ModelMain.sortByAsc)
                    mainAdapter.notifyDataSetChanged()
                } else {
                    rvListDongeng.visibility = View.GONE
                }
                showLoading(false)
            } else {
                showLoading(false)
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

    override fun onResume() {
        super.onResume()
        getDataDongeng()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}