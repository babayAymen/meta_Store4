package com.aymen.store.model.repository.remoteRepository.providerRepository

import com.aymen.store.model.repository.globalRepository.ServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ProviderRepositoryImpl @Inject constructor(
    private val api : ServiceApi
) : ProviderRepository {
    override suspend fun addProvider(provider: String, file: File) {
        withContext(Dispatchers.IO){
            api.addProvider(
                provider,
                file = MultipartBody.Part
                    .createFormData(
                        "file",
                        file.name,
                        file.asRequestBody()
                    )
            )
        }
    }

    override suspend fun addProviderWithoutImage(provider: String) = api.addProviderWithoutImage(provider)
}