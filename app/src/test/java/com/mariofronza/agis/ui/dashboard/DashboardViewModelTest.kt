package com.mariofronza.agis.ui.dashboard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mariofronza.agis.api.FakeApiService
import com.mariofronza.agis.data.DashboardRepository
import com.mariofronza.agis.models.DashboardInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType
import okhttp3.ResponseBody
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
    fun setUp() = Dispatchers.setMain(TestCoroutineDispatcher())

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
                Response.error(401, ResponseBody.create(MediaType.get("application/json"), """{"message":"Token inválido"}"""))
        })

        viewModel.dashboardInfo("token")

        assertEquals("Token inválido", viewModel.error.value)
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
