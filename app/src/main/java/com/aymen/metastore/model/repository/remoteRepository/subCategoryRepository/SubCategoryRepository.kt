package com.aymen.store.model.repository.remoteRepository.subCategoryRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.SubCategory
import com.aymen.metastore.model.entity.dto.SubCategoryDto
import com.aymen.metastore.model.entity.roomRelation.SubCategoryWithCategory
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.io.File

interface SubCategoryRepository {
    suspend fun getSubCategoryByCategory(id : Long,companyId : Long): Response<List<SubCategoryDto>>
    suspend fun addSubCtagoryWithImage(sousCategory : String, file : File)
    suspend fun addSubCategoryWithoutImage(sousCategory : String)


      fun getAllSubCategories(): Flow<PagingData<SubCategoryWithCategory>>
}
