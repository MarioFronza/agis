package com.mariofronza.agis.api

import com.mariofronza.agis.models.*
import retrofit2.Response

open class FakeApiService : ApiService {
    override suspend fun createSession(request: SessionRequest): Response<SessionResponse> = TODO()
    override suspend fun refreshSession(token: String): Response<SessionResponse> = TODO()
    override suspend fun createUser(request: UserRequest): Response<User> = TODO()
    override suspend fun fullRetrieve(token: String): Response<DashboardInfoResponse> = TODO()
    override suspend fun dashboardInfo(token: String): Response<DashboardInfoResponse> = TODO()
    override suspend fun dashboard(token: String, year: String, term: String): Response<Dashboard> = TODO()
    override suspend fun retrieve(token: String, year: String, term: String): Response<DashboardInfoResponse> = TODO()
}
