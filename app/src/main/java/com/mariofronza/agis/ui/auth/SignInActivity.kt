package com.mariofronza.agis.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mariofronza.agis.api.ApiService
import com.mariofronza.agis.data.SessionRepository
import com.mariofronza.agis.databinding.ActivitySignInBinding
import com.mariofronza.agis.models.SessionRequest
import com.mariofronza.agis.ui.MainActivity
import com.mariofronza.agis.utils.SessionManager

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var sessionManager: SessionManager
    private val viewModel: AuthViewModel by viewModels {
        viewModelFactory { initializer { AuthViewModel(SessionRepository(ApiService())) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        viewModel.session.observe(this) { session ->
            sessionManager.save(session)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        viewModel.error.observe(this) { message ->
            binding.btnSignIn.isEnabled = true
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        binding.btnSignIn.setOnClickListener { validateFields() }
        binding.tvCreateAccount.setOnClickListener { startActivity(Intent(this, SignUpActivity::class.java)) }
    }

    private fun validateFields() {
        val email = binding.etSignInEmail.text.toString()
        val password = binding.etSignInPassword.text.toString()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        } else {
            binding.btnSignIn.isEnabled = false
            viewModel.signIn(SessionRequest(email, password))
        }
    }
}
