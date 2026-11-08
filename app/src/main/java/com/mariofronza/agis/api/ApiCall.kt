package com.mariofronza.agis.api

import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class ApiError(val status: Int?, message: String) : Exception(message)

suspend fun <T> safeCall(call: suspend () -> Response<T>): Result<T> = try {
    val response = call()
    val body = response.body()
    if (response.isSuccessful && body != null) Result.success(body)
    else Result.failure(ApiError(response.code(), errorMessage(response.errorBody()?.string())))
} catch (e: IOException) {
    Result.failure(ApiError(null, "Falha de conexão com o servidor"))
}

private fun errorMessage(errorBody: String?): String = try {
    JSONObject(errorBody.orEmpty()).getString("message")
} catch (e: JSONException) {
    "Erro na requisição"
}
