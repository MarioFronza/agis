package com.mariofronza.agis.data

import com.mariofronza.agis.api.ApiService
import com.mariofronza.agis.models.SessionRequest
import com.mariofronza.agis.models.UserRequest

class SessionRepository(
    private val api: ApiService
) {

    suspend fun createSession(sessionRequest: SessionRequest) = api.createSession(sessionRequest)

    suspend fun createUser(userRequest: UserRequest) = api.createUser(userRequest)

}