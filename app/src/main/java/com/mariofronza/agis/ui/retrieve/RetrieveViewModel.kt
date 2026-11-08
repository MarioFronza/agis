package com.mariofronza.agis.ui.retrieve

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariofronza.agis.api.safeCall
import com.mariofronza.agis.data.RetrieveRepository
import com.mariofronza.agis.models.DashboardInfoResponse
import kotlinx.coroutines.launch

class RetrieveViewModel(
    private val retrieveRepository: RetrieveRepository
) : ViewModel() {

    private val _retrieveResult = MutableLiveData<DashboardInfoResponse>()
    private val _error = MutableLiveData<String>()

    val retrieveResult: LiveData<DashboardInfoResponse> = _retrieveResult
    val error: LiveData<String> = _error

    fun retrieve(token: String, year: String, term: String) = viewModelScope.launch {
        safeCall { retrieveRepository.startRetrieve(token, year, term) }
            .onSuccess { _retrieveResult.value = it }
            .onFailure { _error.value = it.message }
    }

    fun fullRetrieve(token: String) = viewModelScope.launch {
        safeCall { retrieveRepository.startFullRetrieve(token) }
            .onSuccess { _retrieveResult.value = it }
            .onFailure { _error.value = it.message }
    }
}
