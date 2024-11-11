package com.aymen.store.model.repository.remoteRepository.subCategoryRepository

import com.aymen.store.model.repository.globalRepository.ServiceApi
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class SubCategoryRepositoryImpl  @Inject constructor(
    private val api : ServiceApi
)
    :SubCategoryRepository{
    override suspend fun getSubCategoryByCategory(id : Long, companyId : Long) =  api.getAllSubCategoryByCategory(id, companyId = companyId)
    override suspend fun getAllSubCategories(companyId : Long) = api.getAllSubCategories(companyId = companyId)
    override suspend fun addSubCtagoryWithImage(sousCategory: String, file: File) {
        api.addSubCategoryWithImage(sousCategory,
            file = MultipartBody.Part
                .createFormData(
                    "file",
                    file.name,
                    file.asRequestBody()
                )
        )
    }

    override suspend fun addSubCategoryWithoutImage(sousCategory: String) = api.addSubCategoryWithoutImage(sousCategory)

}