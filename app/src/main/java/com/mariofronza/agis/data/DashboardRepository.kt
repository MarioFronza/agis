package com.mariofronza.agis.data

import com.mariofronza.agis.api.ApiService

class DashboardRepository(
    private val api: ApiService
) {
    suspend fun dashboardInfo(token: String) = api.dashboardInfo("Bearer $token")

    suspend fun dashboard(token: String, year: String, term: String) =
        api.dashboard("Bearer $token", year, term)

}