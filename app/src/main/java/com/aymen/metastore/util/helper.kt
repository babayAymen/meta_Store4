package com.aymen.metastore.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

fun String.removeHtmlTags(): String {
    val regex = Regex("<[^>]*>|<\\w+>|</\\w+>")
    return regex.replace(this, "")
}

inline fun <Result : Any, Request> networkBoundResource(
    crossinline databaseQuery: () -> Flow<Result>,
    crossinline apiCall: suspend () -> Request,
    crossinline saveApiCallResult: suspend (Request) -> Unit,
    crossinline shouldFetch: (Result) -> Boolean = { true },
    crossinline onFetchSuccess: () -> Unit = {},
    crossinline onFetchFailed: (Throwable) -> Unit = {}
): Flow<Resource<Result>> = channelFlow{
    val data = databaseQuery().first()
    if(shouldFetch(data)){
        val loading = launch {
            databaseQuery().collect { send(Resource.Loading(it))
            }
        }

        try {
            saveApiCallResult(apiCall())
            onFetchSuccess()
            loading.cancel()
            databaseQuery().collect { send(Resource.Success(it)) }

        }catch (th : Throwable){
            onFetchFailed(th)
            loading.cancel()
            databaseQuery().collect { send(Resource.Error(it,th)) }
        }
    }else{
        databaseQuery().collect { send(Resource.Success(it)) }

    }
}
