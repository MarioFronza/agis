package com.mariofronza.agis.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.mariofronza.agis.R
import com.mariofronza.agis.api.ApiService
import com.mariofronza.agis.data.SessionRepository
import com.mariofronza.agis.ui.auth.AuthViewModel
import com.mariofronza.agis.ui.auth.SignInActivity
import com.mariofronza.agis.utils.SessionManager
import com.onesignal.OneSignal
import org.json.JSONObject

class SessionActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)

        sessionManager = SessionManager(this)
        val factory = ViewModelFactory { AuthViewModel(SessionRepository(ApiService())) }
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        viewModel.session.observe(this) { session ->
            sessionManager.save(session)
            goTo(MainActivity::class.java)
        }

        viewModel.error.observe(this) { message ->
            Log.i("SessionActivity", "Refresh token rejected: $message")
            signOut()
        }

        sessionManager.fetchRefreshToken()
            ?.let(viewModel::refreshToken)
            ?: goTo(SignInActivity::class.java)
    }

    // OneSignal callbacks run off the main thread.
    private fun signOut() {
        OneSignal.removeExternalUserId(object : OneSignal.OSExternalUserIdUpdateCompletionHandler {
            override fun onSuccess(results: JSONObject?) = runOnUiThread(::clearSession)
            override fun onFailure(error: OneSignal.ExternalIdError?) = runOnUiThread(::clearSession)
        })
    }

    private fun clearSession() {
        sessionManager.clear()
        goTo(SignInActivity::class.java)
    }

    private fun goTo(activity: Class<*>) {
        startActivity(Intent(this, activity))
        finish()
    }
}
