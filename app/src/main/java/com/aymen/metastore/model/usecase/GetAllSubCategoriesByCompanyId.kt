package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.SubCategoryWithCategory
import com.aymen.store.model.repository.remoteRepository.subCategoryRepository.SubCategoryRepository
import kotlinx.coroutines.flow.Flow

class GetAllSubCategoriesByCompanyId(private val repository : SubCategoryRepository) {
    operator fun invoke(companyId : Long) : Flow<PagingData<SubCategoryWithCategory>>{
        return repository.getAllSubCategoriesByCompanyId(companyId)

    }
}