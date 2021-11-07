package com.mariofronza.agis.api

import com.mariofronza.agis.models.*
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ApiService {

    companion object {
        operator fun invoke(): ApiService {
            val clientSetup = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build()

            return Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8090")
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientSetup)
                .build()
                .create(ApiService::class.java)
        }
    }

    @POST("sessions")
    suspend fun createSession(@Body request: SessionRequest): Response<SessionResponse>

    @POST("users")
    suspend fun createUser(@Body request: UserRequest): Response<User>

    @GET("retrieve/full")
    suspend fun fullRetrieve(
        @Header("Authorization") token: String,
    ): Response<DashboardInfoResponse>

    @GET("dashboard/info")
    suspend fun dashboardInfo(
        @Header("Authorization") token: String,
    ): Response<DashboardInfoResponse>

    @GET("dashboard")
    suspend fun dashboard(
        @Header("Authorization") token: String,
        @Query("year") year: String,
        @Query("term") term: String,
    ): Response<Dashboard>


    @GET("retrieve")
    suspend fun retrieve(
        @Header("Authorization") token: String,
        @Query("year") year: String,
        @Query("term") term: String,
    ): Response<Boolean>


}