package com.azhar.dongeng.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azhar.dongeng.R
import com.azhar.dongeng.adapter.MainAdapter
import com.azhar.dongeng.db.FavoriteHelper
import com.azhar.dongeng.model.ModelMain
import java.util.Collections

class FavoriteActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var rvListDongeng: RecyclerView
    private lateinit var mainAdapter: MainAdapter
    private lateinit var favoriteHelper: FavoriteHelper
    private var listFavorite: ArrayList<ModelMain> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        toolbar = findViewById(R.id.toolbarFavorite)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        favoriteHelper = FavoriteHelper.getInstance(this)
        favoriteHelper.open()

        rvListDongeng = findViewById(R.id.rvListDongeng)
        rvListDongeng.layoutManager = LinearLayoutManager(this)
        rvListDongeng.setHasFixedSize(true)
        getDataDongeng()
    }

    private fun getDataDongeng() {
        listFavorite = favoriteHelper.queryAll()
        if (listFavorite.size > 0) {
            rvListDongeng.visibility = View.VISIBLE
            mainAdapter = MainAdapter(this, listFavorite)
            rvListDongeng.adapter = mainAdapter
            Collections.sort(listFavorite, ModelMain.sortByAsc)
            mainAdapter.notifyDataSetChanged()
        } else {
            rvListDongeng.visibility = View.GONE
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