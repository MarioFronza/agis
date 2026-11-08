package com.mariofronza.agis.ui.retrieve

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mariofronza.agis.R
import com.mariofronza.agis.api.ApiService
import com.mariofronza.agis.data.RetrieveRepository
import com.mariofronza.agis.ui.MainActivity
import com.mariofronza.agis.utils.SessionManager

class RetrieveActivity : AppCompatActivity() {

    private val viewModel: RetrieveViewModel by viewModels {
        viewModelFactory { initializer { RetrieveViewModel(RetrieveRepository(ApiService())) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_await)

        viewModel.retrieveResult.observe(this) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        viewModel.error.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            finish()
        }

        if (savedInstanceState == null) SessionManager(this).fetchAccessToken()?.let(viewModel::fullRetrieve)
    }
}
