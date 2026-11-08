package com.mariofronza.agis.ui.auth

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
import kotlinx.coroutines.launch

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
            .onSuccess {
                // Push is best effort: OneSignal may never answer without Play Services.
                OneSignal.setExternalUserId(it.userId)
                _session.value = it
            }
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
}
