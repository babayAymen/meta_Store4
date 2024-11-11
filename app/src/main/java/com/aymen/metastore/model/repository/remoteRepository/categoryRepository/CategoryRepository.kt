package com.aymen.store.model.repository.remoteRepository.categoryRepository

import com.aymen.store.model.entity.dto.CategoryDto
import retrofit2.Response
import java.io.File

interface CategoryRepository {


    suspend fun getAllCategoryByCompany(companyId : Long): Response<List<CategoryDto>>
    suspend fun addCategoryApiWithImage(category : String, file : File)

    suspend fun addCategoryApiWithoutImeg(category:String)
}