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
import com.mariofronza.agis.models.SigaCredential
import com.mariofronza.agis.models.UserRequest
import com.mariofronza.agis.ui.retrieve.RetrieveActivity
import com.mariofronza.agis.utils.SessionManager
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var factory: AuthViewModelFactory
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val api = ApiService()
        val sessionRepository = SessionRepository(api)

        sessionManager = SessionManager(this)
        factory = AuthViewModelFactory(sessionRepository)
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)


        viewModel.user.observe(this, Observer {
            goToAwaitFragment()
        })

        viewModel.error.observe(this, Observer { message ->
            signUpFail(message)
        })

        tvSignIn.setOnClickListener {
            goToSignInActivity()
        }

        btnSignUp.setOnClickListener {
            validateFields()
        }
    }


    private fun validateFields() {
        val email = etEmailSignUp.text.toString()
        val password = etPasswordSignUp.text.toString()
        val sigaCpf = etCpfSignUp.text.toString()
        val sigaPassword = etSigaPasswordSignUp.text.toString()


        if (email.isEmpty() || email.isEmpty() || password.isBlank() || sigaCpf.isBlank() || sigaPassword.isBlank()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        } else {
            signUp(email, password, sigaCpf, sigaPassword)
        }
    }

    private fun signUp(email: String, password: String, sigaCpf: String, sigaPassword: String) {
        val userRequest = UserRequest(
            email = email,
            password = password,
            sigaCredential = SigaCredential(
                cpf = sigaCpf,
                password = sigaPassword
            )
        )
        viewModel.signUp(userRequest)
    }

    private fun signUpFail(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun goToAwaitFragment() {
        Intent(this, RetrieveActivity::class.java).also {
            startActivity(it)
        }
    }

    private fun goToSignInActivity() {
        Intent(this, SignInActivity::class.java).also {
            startActivity(it)
        }
    }
}