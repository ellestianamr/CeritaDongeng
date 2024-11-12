package com.azhar.dongeng.utils

import android.content.Context
import android.content.SharedPreferences
import com.azhar.dongeng.utils.Constant.KEY_LOGIN
import com.azhar.dongeng.utils.Constant.PREFS_NAME

object SharedPreference {

    fun initPref(context: Context, name: String): SharedPreferences {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    private fun editorPreference(context: Context, name: String): SharedPreferences.Editor {
        val sharedPref = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        return sharedPref.edit()
    }

    fun setBooleanPref(prefBoolean: String, value: Boolean, context: Context) {
        val editor = editorPreference(context, PREFS_NAME)
        editor.putBoolean(prefBoolean, value)
        editor.apply()
    }

    fun setStringPref(prefString: String, value: String, context: Context) {
        val editor = editorPreference(context, PREFS_NAME)
        editor.putString(prefString, value)
        editor.apply()
    }

    fun logout(context: Context) {
        val editor = editorPreference(context, PREFS_NAME)
        editor.remove(KEY_LOGIN)
        editor.apply()
    }
}