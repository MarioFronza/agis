package com.mariofronza.agis.data

import com.mariofronza.agis.api.ApiService
import com.mariofronza.agis.models.SessionRequest

class SessionRepository(
    private val api: ApiService
) {

    suspend fun createSession(sessionRequest: SessionRequest) = api.createSession(sessionRequest)

}