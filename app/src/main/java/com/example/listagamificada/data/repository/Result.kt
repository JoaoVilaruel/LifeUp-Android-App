package com.example.listagamificada.data.repository

sealed class UiResult<out T> {
    data class Success<T>(val data: T) : UiResult<T>()
    data class Error(val exception: Throwable) : UiResult<Nothing>()
    object Loading : UiResult<Nothing>()
}
