package com.mariofronza.agis.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mariofronza.agis.data.SessionRepository
import com.mariofronza.agis.models.SessionRequest
import com.mariofronza.agis.models.SessionResponse
import com.mariofronza.agis.utils.RequestErrorFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private var _session = MutableLiveData<SessionResponse>()
    private var _error = MutableLiveData<String>()


    val session: LiveData<SessionResponse>
        get() = _session

    val error: LiveData<String>
        get() = _error


    fun singIn(sessionRequest: SessionRequest) {
        CoroutineScope(Dispatchers.Main).launch {
            val response = sessionRepository.createSession(sessionRequest)
            if (response.isSuccessful) {
                _session.value = response.body()
            } else {
                val errorMessage =
                    RequestErrorFormatter.formatErrorBody(response.errorBody()!!.string())
                _error.value = errorMessage
            }
        }
    }

}