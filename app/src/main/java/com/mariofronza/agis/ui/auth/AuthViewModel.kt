package com.mariofronza.agis.ui.auth

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
import org.json.JSONException

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
                _session.value = response.body()
            } else {
                val errorMessage =
                    RequestErrorFormatter.formatErrorBody(response.errorBody()!!.string())
                _error.value = errorMessage
            }
        }
    }


    fun signUp(userRequest: UserRequest) {
        CoroutineScope(Dispatchers.Main).launch {
            val response = sessionRepository.createUser(userRequest)
            if (response.isSuccessful) {
                _user.value = response.body()
                val externalUserId = _user.value?.id
                setOneSignalExternalUserId(externalUserId.toString())
            } else {
                val errorMessage =
                    RequestErrorFormatter.formatErrorBody(response.errorBody()!!.string())
                _error.value = errorMessage
            }
        }
    }

    private fun setOneSignalExternalUserId(externalUserId: String) {
        OneSignal.setExternalUserId(
            externalUserId,
            object : OSExternalUserIdUpdateCompletionHandler {
                override fun onSuccess(results: JSONObject) {
                    try {
                        OneSignal.onesignalLog(
                            OneSignal.LOG_LEVEL.VERBOSE,
                            "Set external user id successfully"
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
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