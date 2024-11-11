package com.aymen.store.model.repository.remoteRepository.subCategoryRepository

import com.aymen.store.model.entity.dto.SubCategoryDto
import retrofit2.Response
import java.io.File

interface SubCategoryRepository {
    suspend fun getSubCategoryByCategory(id : Long,companyId : Long): Response<List<SubCategoryDto>>
    suspend fun getAllSubCategories(companyId : Long): Response<List<SubCategoryDto>>
    suspend fun addSubCtagoryWithImage(sousCategory : String, file : File)
    suspend fun addSubCategoryWithoutImage(sousCategory : String)
}