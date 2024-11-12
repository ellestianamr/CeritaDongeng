package com.azhar.dongeng.db

import android.provider.BaseColumns

internal class DatabaseContract {

    internal class FavoriteColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "favorite"
            const val ID = "id"
            const val TITLE = "title"
            const val FILE = "file"
        }
    }
}