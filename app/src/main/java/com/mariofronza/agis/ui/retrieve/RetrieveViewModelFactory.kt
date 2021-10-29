package com.mariofronza.agis.ui.retrieve

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mariofronza.agis.data.RetrieveRepository

class RetrieveViewModelFactory(
    private val retrieveRepository: RetrieveRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RetrieveViewModel(retrieveRepository) as T
    }
}