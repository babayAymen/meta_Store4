package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.ArticleCompanyDto
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.store.model.repository.remoteRepository.articleRepository.ArticleRepository
import kotlinx.coroutines.flow.Flow

class GetArticlesByCompanyAndCategoryOrSubCategory(private val repository : ArticleRepository) {

    operator fun invoke(companyId : Long , categoryId : Long , subCategoryId : Long) : Flow<PagingData<ArticleCompany>>{
        return repository.getArticlesByCompanyAndCategoryOrSubCategory(companyId, categoryId, subCategoryId)
    }
}