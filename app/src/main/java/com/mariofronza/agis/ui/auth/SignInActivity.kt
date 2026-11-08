package com.mariofronza.agis.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.mariofronza.agis.R
import com.mariofronza.agis.api.ApiService
import com.mariofronza.agis.data.SessionRepository
import com.mariofronza.agis.models.SessionRequest
import com.mariofronza.agis.ui.MainActivity
import com.mariofronza.agis.ui.ViewModelFactory
import com.mariofronza.agis.utils.SessionManager
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        sessionManager = SessionManager(this)
        val factory = ViewModelFactory { AuthViewModel(SessionRepository(ApiService())) }
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        viewModel.session.observe(this) { session ->
            sessionManager.save(session)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        viewModel.error.observe(this) { message ->
            btnSignIn.isEnabled = true
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        btnSignIn.setOnClickListener { validateFields() }
        tvCreateAccount.setOnClickListener { startActivity(Intent(this, SignUpActivity::class.java)) }
    }

    private fun validateFields() {
        val email = etSignInEmail.text.toString()
        val password = etSignInPassword.text.toString()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        } else {
            btnSignIn.isEnabled = false
            viewModel.signIn(SessionRequest(email, password))
        }
    }
}
