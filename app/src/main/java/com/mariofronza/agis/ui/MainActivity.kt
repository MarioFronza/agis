package com.mariofronza.agis.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.mariofronza.agis.R
import com.mariofronza.agis.api.ApiService
import com.mariofronza.agis.data.SessionRepository
import com.mariofronza.agis.models.SessionResponse
import com.mariofronza.agis.ui.auth.AuthViewModel
import com.mariofronza.agis.ui.auth.AuthViewModelFactory
import com.mariofronza.agis.ui.auth.SignInActivity
import com.mariofronza.agis.utils.SessionManager
import com.onesignal.OneSignal
import com.onesignal.OneSignal.OSExternalUserIdUpdateCompletionHandler

import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var factory: AuthViewModelFactory
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)

        val api = ApiService()
        val sessionRepository = SessionRepository(api)

        factory = AuthViewModelFactory(sessionRepository)
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        viewModel.session.observe(this, Observer { sessionResponse ->
            refreshTokenSuccess(sessionResponse)
        })

        viewModel.error.observe(this, Observer { _ ->
            refreshTokenFail()
        })

    }

    private fun refreshTokenSuccess(sessionResponse: SessionResponse) {
        sessionManager.saveAccessToken(sessionResponse.accessToken)
        sessionManager.saveRefreshToken(sessionResponse.refreshToken)
        sessionManager.saveUserId(sessionResponse.userId)
        Log.i("MainActivity", "Saved sessions attrs")
    }

    private fun refreshTokenFail() {
        Log.i("REFRESH_TOKEN", "Error when try to receive new regresh token")
        OneSignal.removeExternalUserId(object : OSExternalUserIdUpdateCompletionHandler {
            override fun onSuccess(results: JSONObject?) {
                Log.i("REFRESH_TOKEN", "Removed external user id from on signal")
                signOut()
            }

            override fun onFailure(error: OneSignal.ExternalIdError?) {
                Log.i("REFRESH_TOKEN", "Error when try remove external user id from on signal")
                signOut()
            }
        })
    }

    private fun signOut() {
        sessionManager.removeTokens()
        goToSignInActivity()
    }

    override fun onStart() {
        super.onStart()
        val refreshToken = sessionManager.fetchRefreshToken()
        if (refreshToken == null) {
            goToSignInActivity()
        } else {
            tryRefreshToken(refreshToken)
        }
    }

    private fun tryRefreshToken(refreshToken: String) {
        viewModel.refreshToken(refreshToken)
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.navHostFragment).navigateUp()
    }

    private fun goToSignInActivity() {
        Intent(this, SignInActivity::class.java).also {
            startActivity(it)
        }
    }

}