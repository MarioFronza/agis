package com.mariofronza.agis.data

import com.mariofronza.agis.api.ApiService

class RetrieveRepository(
    private val api: ApiService
) {

    suspend fun startRetrieve(token: String) = api.fullRetrieve("Bearer $token")

}