package com.mariofronza.agis.api

import com.mariofronza.agis.models.SessionRequest
import com.mariofronza.agis.models.SessionResponse
import com.mariofronza.agis.models.User
import com.mariofronza.agis.models.UserRequest
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

    @GET("retrieve")
    suspend fun retrieve(): Response<Boolean>


}