package com.mariofronza.agis.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariofronza.agis.api.safeCall
import com.mariofronza.agis.data.DashboardRepository
import com.mariofronza.agis.models.Dashboard
import com.mariofronza.agis.models.DashboardInfoResponse
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _dashboardInfoResult = MutableLiveData<DashboardInfoResponse>()
    private val _dashboardResult = MutableLiveData<Dashboard>()
    private val _error = MutableLiveData<String>()

    val dashboardInfoResult: LiveData<DashboardInfoResponse> = _dashboardInfoResult
    val dashboardResult: LiveData<Dashboard> = _dashboardResult
    val error: LiveData<String> = _error

    fun dashboardInfo(token: String) = viewModelScope.launch {
        safeCall { dashboardRepository.dashboardInfo(token) }
            .onSuccess { _dashboardInfoResult.value = it }
            .onFailure { _error.value = it.message }
    }

    fun dashboard(token: String, year: String, term: String) = viewModelScope.launch {
        safeCall { dashboardRepository.dashboard(token, year, term) }
            .onSuccess { _dashboardResult.value = it }
            .onFailure { _error.value = it.message }
    }
}
