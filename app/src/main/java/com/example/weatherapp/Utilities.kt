package com.example.weatherapp

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

fun <T> apiFlow(
    block: suspend () -> Response<T>
): Flow<Result<T>> = flow {
    try {
        val response = block()
        Log.d("HTTP", response.toString())
        if (response.isSuccessful) {
            val data = response.body()
            Log.d("HTTP", response.body().toString())
            if (data != null) {
                emit(Result.Success(data))
            } else {
                emit(Result.Error("Response body is null"))
            }
        } else {
            emit(Result.Error("Request failed with HTTP error code: ${response.code()}"))
        }
    } catch (e: Exception) {
        emit(Result.Error(e.message ?: "Unknown error occurred"))
    }
}
