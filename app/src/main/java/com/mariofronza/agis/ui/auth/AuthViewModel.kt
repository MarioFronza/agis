package com.mariofronza.agis.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mariofronza.agis.data.SessionRepository
import com.mariofronza.agis.models.SessionRequest
import com.mariofronza.agis.models.SessionResponse
import com.mariofronza.agis.models.User
import com.mariofronza.agis.models.UserRequest
import com.mariofronza.agis.utils.RequestErrorFormatter
import com.onesignal.OneSignal
import com.onesignal.OneSignal.ExternalIdError
import com.onesignal.OneSignal.OSExternalUserIdUpdateCompletionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import org.json.JSONObject


class AuthViewModel(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private var _session = MutableLiveData<SessionResponse>()
    private var _user = MutableLiveData<User>()
    private var _error = MutableLiveData<String>()

    val session: LiveData<SessionResponse>
        get() = _session

    val user: LiveData<User>
        get() = _user


    val error: LiveData<String>
        get() = _error


    fun singIn(sessionRequest: SessionRequest) {
        CoroutineScope(Dispatchers.Main).launch {
            val response = sessionRepository.createSession(sessionRequest)
            if (response.isSuccessful) {
                val externalUserId = _session.value?.userId
                setOneSignalExternalUserId(externalUserId.toString(), response.body()!!)
            } else {
                val errorMessage =
                    RequestErrorFormatter.formatErrorBody(response.errorBody()!!.string())
                _error.value = errorMessage
            }
        }
    }

    fun refreshToken(refreshToken: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val response = sessionRepository.refreshToken(refreshToken)
            if (response.isSuccessful) {
                Log.i("REFRESH_TOKEN", response.body().toString())
                _session.value = response.body()
            } else {
                val errorMessage =
                    RequestErrorFormatter.formatErrorBody(response.errorBody()!!.string())
                Log.i("REFRESH_TOKEN", errorMessage)
                _error.value = errorMessage
            }
        }
    }

    fun signUp(userRequest: UserRequest) {
        CoroutineScope(Dispatchers.Main).launch {
            val response = sessionRepository.createUser(userRequest)
            if (response.isSuccessful) {
                _user.value = response.body()
            } else {
                val errorMessage =
                    RequestErrorFormatter.formatErrorBody(response.errorBody()!!.string())
                _error.value = errorMessage
            }
        }
    }

    private fun setOneSignalExternalUserId(externalUserId: String, body: SessionResponse) {
        OneSignal.setExternalUserId(
            externalUserId,
            object : OSExternalUserIdUpdateCompletionHandler {
                override fun onSuccess(results: JSONObject) {
                    _session.value = body
                }

                override fun onFailure(error: ExternalIdError) {
                    OneSignal.onesignalLog(
                        OneSignal.LOG_LEVEL.VERBOSE,
                        "Set external user id done with error: $error"
                    )
                }
            })
    }

}