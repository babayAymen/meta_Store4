package com.aymen.store.model.repository.remoteRepository.providerRepository

import android.util.Log
import com.aymen.metastore.model.entity.dto.ClientProviderRelationDto
import com.aymen.metastore.model.entity.dto.CompanyDto
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class ProviderRepositoryImpl @Inject constructor(
    private val api : ServiceApi
) : ProviderRepository {
    override suspend fun addProvider(provider: String, file: File?): Response<ClientProviderRelationDto> {
      return  withContext(Dispatchers.IO){
          if(file == null){
              api.addProviderWithoutImage(provider)
          }else {
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
    }

    override suspend fun updateProvider(
        provider: String,
        file: File?
    ): Response<CompanyDto> {
        return withContext(Dispatchers.IO){
            if(file == null){
                Log.e("updateprovider","without image")
                api.updateProviderWithoutImage(provider)
            }else{
                api.updateProvider(
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
    }

    override suspend fun deleteProvider(id: Long) = api.deleteProvider(id)

//    override suspend fun addProviderWithoutImage(provider: String) = api.addProviderWithoutImage(provider)
}