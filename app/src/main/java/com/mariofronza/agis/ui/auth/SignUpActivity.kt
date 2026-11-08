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
import com.mariofronza.agis.databinding.ActivitySignUpBinding
import com.mariofronza.agis.models.SessionRequest
import com.mariofronza.agis.models.SigaCredential
import com.mariofronza.agis.models.UserRequest
import com.mariofronza.agis.ui.retrieve.RetrieveActivity
import com.mariofronza.agis.utils.SessionManager

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var sessionManager: SessionManager
    private val viewModel: AuthViewModel by viewModels {
        viewModelFactory { initializer { AuthViewModel(SessionRepository(ApiService())) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        viewModel.user.observe(this) { user ->
            viewModel.signIn(SessionRequest(user.email, binding.etPasswordSignUp.text.toString()))
        }

        viewModel.session.observe(this) { session ->
            sessionManager.save(session)
            // Clear the task so the sign in screen underneath doesn't stay in the back stack.
            startActivity(
                Intent(this, RetrieveActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }

        viewModel.error.observe(this) { message ->
            binding.btnSignUp.isEnabled = true
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        binding.tvSignIn.setOnClickListener { finish() }
        binding.btnSignUp.setOnClickListener { validateFields() }
    }

    private fun validateFields() {
        val email = binding.etEmailSignUp.text.toString()
        val password = binding.etPasswordSignUp.text.toString()
        val sigaCpf = binding.etCpfSignUp.text.toString()
        val sigaPassword = binding.etSigaPasswordSignUp.text.toString()

        if (listOf(email, password, sigaCpf, sigaPassword).any(String::isBlank)) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        } else {
            binding.btnSignUp.isEnabled = false
            viewModel.signUp(UserRequest(email = email, password = password, sigaCredential = SigaCredential(sigaCpf, sigaPassword)))
        }
    }
}
