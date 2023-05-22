package com.example.weatherapp.ui.theme

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {

    protected fun launchSuspend(block: suspend CoroutineScope.() -> Unit) {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            // Handle exceptions here, e.g., log or display an error message
            handleException(throwable)
        }

        viewModelScope.launch(exceptionHandler) {
            block()
        }
    }

    protected open fun handleException(throwable: Throwable) {
        Log.d("coroutine", throwable.message ?: "Error occurred! Please try again.")
    }
}
