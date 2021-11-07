package com.mariofronza.agis.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mariofronza.agis.data.DashboardRepository
import com.mariofronza.agis.data.RetrieveRepository
import com.mariofronza.agis.models.Dashboard
import com.mariofronza.agis.models.DashboardInfoResponse
import com.mariofronza.agis.utils.RequestErrorFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private var _dashboardInfoResult = MutableLiveData<DashboardInfoResponse>()
    private var _dashboardResult = MutableLiveData<Dashboard>()
    private var _error = MutableLiveData<String>()

    val dashboardInfoResult: LiveData<DashboardInfoResponse>
        get() = _dashboardInfoResult

    val dashboardResult: LiveData<Dashboard>
        get() = _dashboardResult

    val error: LiveData<String>
        get() = _error

    fun dashboardInfo(token: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val response = dashboardRepository.dashboardInfo(token)
            if (response.isSuccessful) {
                _dashboardInfoResult.value = response.body()
            } else {
                val errorMessage =
                    RequestErrorFormatter.formatErrorBody(response.errorBody()!!.string())
                _error.value = errorMessage
            }
        }
    }


    fun dashboard(token: String, year: String, term: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val response = dashboardRepository.dashboard(token, year, term)
            if (response.isSuccessful) {
                _dashboardResult.value = response.body()
            } else {
                val errorMessage =
                    RequestErrorFormatter.formatErrorBody(response.errorBody()!!.string())
                _error.value = errorMessage
            }
        }
    }

}