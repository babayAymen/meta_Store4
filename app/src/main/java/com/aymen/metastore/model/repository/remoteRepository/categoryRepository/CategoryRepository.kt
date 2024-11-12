package com.aymen.store.model.repository.remoteRepository.categoryRepository

import androidx.paging.PagingData
import com.aymen.store.model.entity.dto.CategoryDto
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.io.File

interface CategoryRepository {


    suspend fun getAllCategoryByCompany(companyId : Long): Response<List<CategoryDto>>
    suspend fun addCategoryApiWithImage(category : String, file : File)
    suspend fun addCategoryApiWithoutImeg(category:String)

    fun getAllCategory(pageSize : Int): Flow<PagingData<CategoryDto>>
}