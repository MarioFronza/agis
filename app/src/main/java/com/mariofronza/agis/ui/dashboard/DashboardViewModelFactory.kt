package com.mariofronza.agis.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mariofronza.agis.data.DashboardRepository
import com.mariofronza.agis.data.RetrieveRepository

class DashboardViewModelFactory(
    private val retrieveRepository: DashboardRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DashboardViewModel(retrieveRepository) as T
    }
}