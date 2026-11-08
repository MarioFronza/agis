package com.mariofronza.agis.utils

import android.content.Context
import android.content.SharedPreferences
import com.mariofronza.agis.R
import com.mariofronza.agis.models.SessionResponse

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    fun save(session: SessionResponse) = prefs.edit()
        .putString(ACCESS_TOKEN, session.accessToken)
        .putString(REFRESH_TOKEN, session.refreshToken)
        .putString(USER_ID, session.userId)
        .apply()

    fun clear() = prefs.edit().clear().apply()

    fun fetchAccessToken(): String? = prefs.getString(ACCESS_TOKEN, null)

    fun fetchRefreshToken(): String? = prefs.getString(REFRESH_TOKEN, null)

    companion object {
        const val ACCESS_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"
        const val USER_ID = "user_id"
    }
}
