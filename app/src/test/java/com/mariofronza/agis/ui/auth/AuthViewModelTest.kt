package com.mariofronza.agis.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mariofronza.agis.api.FakeApiService
import com.mariofronza.agis.data.SessionRepository
import com.mariofronza.agis.models.SessionRequest
import com.mariofronza.agis.models.SessionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun rejected_credentials_are_reported_as_invalid_login() {
        // Spring's default error body: no "message" field.
        val springUnauthorized = """{"timestamp":"2026-11-08T15:24:19.361Z","status":401,"error":"Unauthorized","path":"/sessions"}"""
        val viewModel = AuthViewModel(SessionRepository(object : FakeApiService() {
            override suspend fun createSession(request: SessionRequest): Response<SessionResponse> =
                Response.error(401, springUnauthorized.toResponseBody("application/json".toMediaType()))
        }))

        viewModel.signIn(SessionRequest("a@b.com", "wrong"))

        assertEquals("E-mail ou senha inválidos", viewModel.error.value)
    }
}
