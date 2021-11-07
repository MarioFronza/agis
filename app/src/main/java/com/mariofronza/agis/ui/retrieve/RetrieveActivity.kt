package com.mariofronza.agis.ui.retrieve

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mariofronza.agis.R
import com.mariofronza.agis.api.ApiService
import com.mariofronza.agis.data.RetrieveRepository
import com.mariofronza.agis.ui.MainActivity
import com.mariofronza.agis.ui.auth.AuthViewModel
import com.mariofronza.agis.ui.auth.AuthViewModelFactory
import com.mariofronza.agis.utils.SessionManager

class RetrieveActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var factory: RetrieveViewModelFactory
    private lateinit var viewModel: RetrieveViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_await)

        val api = ApiService()
        val retrieveRepository = RetrieveRepository(api)

        sessionManager = SessionManager(this)
        factory = RetrieveViewModelFactory(retrieveRepository)
        viewModel = ViewModelProvider(this, factory).get(RetrieveViewModel::class.java)

        viewModel.retrieveResult.observe(this, Observer {
            goToMainActivity()
        })

        viewModel.error.observe(this, Observer { message ->
            retrieveFail(message)
        })

        startRetrieve()
    }

    private fun startRetrieve() {
        sessionManager.fetchAccessToken()?.let {
            viewModel.retrieve(it)
        }
    }

    private fun goToMainActivity() {
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
        }
    }

    private fun retrieveFail(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }

}