package com.aymen.metastore.util

sealed class Resource<T>(
    val data : T? = null,
    val error : Throwable? = null
) {
    class Success<T>(data: T):Resource<T>(data)
    class Loading<T>(data: T? = null):Resource<T>(data)
    class Error<T>(data: T? = null, error: Throwable? = null):Resource<T>(data,error)
}