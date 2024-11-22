package com.azhar.dongeng.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.azhar.dongeng.db.DatabaseContract.FavoriteColumns.Companion.FILE
import com.azhar.dongeng.db.DatabaseContract.FavoriteColumns.Companion.ID
import com.azhar.dongeng.db.DatabaseContract.FavoriteColumns.Companion.TABLE_NAME
import com.azhar.dongeng.db.DatabaseContract.FavoriteColumns.Companion.TITLE
import com.azhar.dongeng.model.ModelMain
import java.sql.SQLException

class FavoriteHelper(context: Context) {

    private var databaseHelper: DatabaseHelper = DatabaseHelper(context)
    private lateinit var database: SQLiteDatabase

    @Throws(SQLException::class)
    fun open() {
        database = databaseHelper.writableDatabase
    }

    fun close() {
        databaseHelper.close()

        if (database.isOpen)
            database.close()
    }

    fun queryAll(): ArrayList<ModelMain> {
        val arrayList: ArrayList<ModelMain> = ArrayList()
        val cursor: Cursor = database.query(
            DATABASE_TABLE, null,
            null,
            null,
            null,
            null,
            "$ID ASC",
            null
        )
        cursor.moveToFirst()
        var listFav: ModelMain
        if (cursor.count > 0) {
            do {
                listFav = ModelMain()
                listFav.id = cursor.getInt(cursor.getColumnIndexOrThrow(ID)).toString()
                listFav.strJudul = cursor.getString(cursor.getColumnIndexOrThrow(TITLE))
                listFav.strCerita = cursor.getString(cursor.getColumnIndexOrThrow(FILE))
                arrayList.add(listFav)
                cursor.moveToNext()
            } while (!cursor.isAfterLast)
        }
        cursor.close()
        return arrayList
    }

    fun insert(response: ModelMain): Long {
        val values = ContentValues()
        values.put(ID, response.id)
        values.put(TITLE, response.strJudul)
        values.put(FILE, response.strCerita)

        return database.insert(DATABASE_TABLE, null, values)
    }

    fun delete(title: String): Int {
        return database.delete(TABLE_NAME, "$TITLE = '$title'", null)
    }

    companion object {
        private const val DATABASE_TABLE = TABLE_NAME
        private var INSTANCE: FavoriteHelper? = null

        fun getInstance(context: Context): FavoriteHelper =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: FavoriteHelper(context)
            }
    }
}