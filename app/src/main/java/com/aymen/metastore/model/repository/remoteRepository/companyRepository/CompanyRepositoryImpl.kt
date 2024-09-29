package com.aymen.store.model.repository.remoteRepository.companyRepository

import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.repository.globalRepository.ServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class CompanyRepositoryImpl @Inject constructor(
    private val api : ServiceApi
)
    :CompanyRepository{
    override suspend fun addCompany(company: String, file : File) {
        withContext(Dispatchers.IO){
            api.addCompany(
                company,
                file = MultipartBody.Part
                    .createFormData(
                        "file",
                        file.name,
                        file.asRequestBody()
                    )
            )
        }
    }

    override suspend fun getAllMyProvider(companyId : Long) = api.getAllMyProvider(companyId = companyId)
    override suspend fun getMyParent(companyId : Long) = api.getMyParent(companyId = companyId)
    override suspend fun getMyCompany(companyId: Long) = api.getMyCompany(companyId = companyId)
    override suspend fun getMe() = api.getMe()

    override suspend fun getAllCompaniesContaining(search: String) = api.getAllCompaniesContaining(search)
    override suspend fun updateCompany(company: String, file: File) {
        withContext(Dispatchers.IO){
            api.updateCompany(
                company,
                file = MultipartBody.Part
                    .createFormData(
                        "file",
                        file.name,
                        file.asRequestBody()
                    )
            )
        }
    }

    override suspend fun updateImage(image: File): Response<Void> {
      return  api.updateImage(
            image = MultipartBody.Part
                .createFormData(
                    "file",
                    image.name,
                    image.asRequestBody()
                )
        )
    }


}