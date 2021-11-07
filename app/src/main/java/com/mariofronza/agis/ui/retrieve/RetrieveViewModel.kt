package com.mariofronza.agis.ui.retrieve

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mariofronza.agis.data.RetrieveRepository
import com.mariofronza.agis.models.DashboardInfoResponse
import com.mariofronza.agis.utils.RequestErrorFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RetrieveViewModel(
    private val retrieveRepository: RetrieveRepository
) : ViewModel() {

    private var _retrieveResult = MutableLiveData<DashboardInfoResponse>()
    private var _error = MutableLiveData<String>()

    val retrieveResult: LiveData<DashboardInfoResponse>
        get() = _retrieveResult

    val error: LiveData<String>
        get() = _error

    fun retrieve(token: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val response = retrieveRepository.startRetrieve(token)
            if (response.isSuccessful) {
                _retrieveResult.value = response.body()
            } else {
                val errorMessage =
                    RequestErrorFormatter.formatErrorBody(response.errorBody()!!.string())
                _error.value = errorMessage
            }
        }
    }

}