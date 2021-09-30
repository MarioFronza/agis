package com.mariofronza.agis.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mariofronza.agis.data.SessionRepository


class AuthViewModelFactory(
    private val sessionRepository: SessionRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthViewModel(sessionRepository) as T
    }
}