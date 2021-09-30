package com.mariofronza.agis.utils

import android.content.Context
import android.content.SharedPreferences
import com.mariofronza.agis.R

class SessionManager(context: Context) {

    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    fun saveAccessToken(token: String) {
        val editor = prefs.edit()
        editor.putString(ACCESS_TOKEN, token)
        editor.apply()
    }

    fun removeTokens() {
        val editor = prefs.edit()
        editor.apply()
    }

    fun fetchAccessToken(): String? {
        return prefs.getString(ACCESS_TOKEN, null)
    }


    companion object {
        const val ACCESS_TOKEN = "access_token"
    }

}