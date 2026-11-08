package com.mariofronza.agis.ui.dashboard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mariofronza.agis.api.FakeApiService
import com.mariofronza.agis.data.DashboardRepository
import com.mariofronza.agis.models.DashboardInfoResponse
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
import java.io.IOException

@ExperimentalCoroutinesApi
class DashboardViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModelWith(api: FakeApiService) = DashboardViewModel(DashboardRepository(api))

    @Test
    fun network_failure_is_reported_as_error() {
        val viewModel = viewModelWith(object : FakeApiService() {
            override suspend fun dashboardInfo(token: String): Response<DashboardInfoResponse> =
                throw IOException("timeout")
        })

        viewModel.dashboardInfo("token")

        assertEquals("Falha de conexão com o servidor", viewModel.error.value)
    }

    @Test
    fun error_body_message_is_reported_as_error() {
        val viewModel = viewModelWith(object : FakeApiService() {
            override suspend fun dashboardInfo(token: String): Response<DashboardInfoResponse> =
                Response.error(401, """{"message":"Token inválido"}""".toResponseBody("application/json".toMediaType()))
        })

        viewModel.dashboardInfo("token")

        assertEquals("Token inválido", viewModel.error.value)
    }

    @Test
    fun server_error_without_json_body_is_reported_as_unavailable() {
        val viewModel = viewModelWith(object : FakeApiService() {
            override suspend fun dashboardInfo(token: String): Response<DashboardInfoResponse> =
                Response.error(502, "<html>Bad Gateway</html>".toResponseBody("text/html".toMediaType()))
        })

        viewModel.dashboardInfo("token")

        assertEquals("Servidor indisponível, tente novamente mais tarde", viewModel.error.value)
    }

    @Test
    fun successful_response_is_published() {
        val info = DashboardInfoResponse("Mario", emptyList())
        val viewModel = viewModelWith(object : FakeApiService() {
            override suspend fun dashboardInfo(token: String) = Response.success(info)
        })

        viewModel.dashboardInfo("token")

        assertEquals(info, viewModel.dashboardInfoResult.value)
    }
}
