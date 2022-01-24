package com.mariofronza.agis.data

import com.mariofronza.agis.api.ApiService

class RetrieveRepository(
    private val api: ApiService
) {

    suspend fun startRetrieve(
        token: String,
        year: String,
        term: String
    ) = api.retrieve("Bearer $token", year, term)

    suspend fun startFullRetrieve(token: String) = api.fullRetrieve("Bearer $token")

}