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
import com.mariofronza.agis.models.SigaCredential
import com.mariofronza.agis.models.UserRequest
import com.mariofronza.agis.ui.ViewModelFactory
import com.mariofronza.agis.ui.retrieve.RetrieveActivity
import com.mariofronza.agis.utils.SessionManager
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        sessionManager = SessionManager(this)
        val factory = ViewModelFactory { AuthViewModel(SessionRepository(ApiService())) }
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        viewModel.user.observe(this) { user ->
            viewModel.signIn(SessionRequest(user.email, etPasswordSignUp.text.toString()))
        }

        viewModel.session.observe(this) { session ->
            sessionManager.save(session)
            startActivity(Intent(this, RetrieveActivity::class.java))
            finish()
        }

        viewModel.error.observe(this) { message ->
            btnSignUp.isEnabled = true
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        tvSignIn.setOnClickListener { finish() }
        btnSignUp.setOnClickListener { validateFields() }
    }

    private fun validateFields() {
        val email = etEmailSignUp.text.toString()
        val password = etPasswordSignUp.text.toString()
        val sigaCpf = etCpfSignUp.text.toString()
        val sigaPassword = etSigaPasswordSignUp.text.toString()

        if (listOf(email, password, sigaCpf, sigaPassword).any(String::isBlank)) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        } else {
            btnSignUp.isEnabled = false
            viewModel.signUp(UserRequest(email = email, password = password, sigaCredential = SigaCredential(sigaCpf, sigaPassword)))
        }
    }
}
