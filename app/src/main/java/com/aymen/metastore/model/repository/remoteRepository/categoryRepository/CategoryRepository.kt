package com.aymen.store.model.repository.remoteRepository.categoryRepository

import com.aymen.store.model.entity.realm.Category
import retrofit2.Response
import java.io.File

interface CategoryRepository {


    suspend fun getAllCategoryByCompany(myCompanyId : Long,companyId : Long): Response<List<Category>>

    suspend fun addCategoryApiWithImage(category : String, file : File)

    suspend fun addCategoryApiWithoutImeg(category:String)
}