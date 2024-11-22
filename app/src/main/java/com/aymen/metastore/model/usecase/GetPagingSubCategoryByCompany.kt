package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.SubCategoryDto
import com.aymen.metastore.model.entity.model.SubCategory
import com.aymen.metastore.model.entity.roomRelation.SubCategoryWithCategory
import com.aymen.store.model.repository.remoteRepository.subCategoryRepository.SubCategoryRepository
import kotlinx.coroutines.flow.Flow

class GetPagingSubCategoryByCompany(private val repository: SubCategoryRepository) {
    operator fun invoke(): Flow<PagingData<SubCategoryWithCategory>> {
        return repository.getAllSubCategories()
    }
}