package com.aymen.store.model.repository.remoteRepository.providerRepository

import retrofit2.Response
import java.io.File

interface ProviderRepository {

    suspend fun addProvider(provider : String, file : File)

    suspend fun addProviderWithoutImage(provider : String)

}