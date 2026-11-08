package com.mariofronza.agis.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariofronza.agis.api.safeCall
import com.mariofronza.agis.data.SessionRepository
import com.mariofronza.agis.models.SessionRequest
import com.mariofronza.agis.models.SessionResponse
import com.mariofronza.agis.models.User
import com.mariofronza.agis.models.UserRequest
import com.onesignal.OneSignal
import com.onesignal.OneSignal.ExternalIdError
import com.onesignal.OneSignal.OSExternalUserIdUpdateCompletionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject

class AuthViewModel(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _session = MutableLiveData<SessionResponse>()
    private val _user = MutableLiveData<User>()
    private val _error = MutableLiveData<String>()

    val session: LiveData<SessionResponse> = _session
    val user: LiveData<User> = _user
    val error: LiveData<String> = _error

    fun signIn(sessionRequest: SessionRequest) = viewModelScope.launch {
        safeCall { sessionRepository.createSession(sessionRequest) }
            .onSuccess(::linkPushDevice)
            .onFailure { _error.value = it.message }
    }

    fun refreshToken(refreshToken: String) = viewModelScope.launch {
        safeCall { sessionRepository.refreshToken(refreshToken) }
            .onSuccess { _session.value = it }
            .onFailure { _error.value = it.message }
    }

    fun signUp(userRequest: UserRequest) = viewModelScope.launch {
        safeCall { sessionRepository.createUser(userRequest) }
            .onSuccess { _user.value = it }
            .onFailure { _error.value = it.message }
    }

    // OneSignal callbacks run off the main thread, hence postValue.
    // Push is optional: the session is published even when linking fails.
    private fun linkPushDevice(session: SessionResponse) {
        OneSignal.setExternalUserId(session.userId, object : OSExternalUserIdUpdateCompletionHandler {
            override fun onSuccess(results: JSONObject) {
                _session.postValue(session)
            }

            override fun onFailure(error: ExternalIdError) {
                Log.w("AuthViewModel", "OneSignal external id not set: ${error.message}")
                _session.postValue(session)
            }
        })
    }
}
