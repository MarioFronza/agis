package com.mariofronza.agis.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mariofronza.agis.R
import com.mariofronza.agis.api.ApiService
import com.mariofronza.agis.data.SessionRepository
import com.mariofronza.agis.models.SessionRequest
import com.mariofronza.agis.models.SessionResponse
import com.mariofronza.agis.ui.MainActivity
import com.mariofronza.agis.utils.SessionManager
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var factory: AuthViewModelFactory
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val api = ApiService()
        val sessionRepository = SessionRepository(api)

        sessionManager = SessionManager(this)
        factory = AuthViewModelFactory(sessionRepository)
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        viewModel.session.observe(this, Observer { sessionResponse ->
            signInSuccess(sessionResponse)
        })

        viewModel.error.observe(this, Observer { message ->
            signInFail(message)
        })


        btnSignIn.setOnClickListener {
            validateFields()
        }
    }

    private fun validateFields() {
        val email = etSignInEmail.text.toString()
        val password = etSignInPassword.text.toString()

        if (email.isEmpty() || password.isBlank()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        } else {
            signIn(email, password)
        }
    }

    private fun signIn(email: String, password: String) {
        val session = SessionRequest(email, password)
        viewModel.singIn(session)
    }

    private fun signInSuccess(sessionResponse: SessionResponse) {
        sessionManager.saveAccessToken(sessionResponse.token)
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
        }
        finish()
    }

    private fun signInFail(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}