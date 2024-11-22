package com.aymen.metastore.model.repository.globalRepository

import com.aymen.metastore.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseRepository {

    suspend fun <T> invokeApi(
        apiCall: suspend () -> T):
            Resource<T>{
return withContext(Dispatchers.IO){
    try {
        Resource.Success(apiCall.invoke())
    }catch (ex : Throwable){
        Resource.Error(error = ex)

    }
}
    }
}